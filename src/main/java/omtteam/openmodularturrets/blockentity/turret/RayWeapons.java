package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.RayMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.network.RayPacket;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Instant-hit ray weapons (1.12.2 {@code RayTracingTurret} with {@code LaserTurretTileEntity},
 * {@code RailGunTurretTileEntity} and {@code ArcTurretTileEntity}).
 */
public final class RayWeapons {
    /** 1.12.2 {@code blockRange} when nothing was hit. */
    private static final double NO_BLOCK_HIT = 500;

    /**
     * Per-turret ray visuals and damage types; the damage itself comes from {@link RayMath}.
     *
     * @param normalType damage type of the normal part ({@link RayMath#split}), null if the turret deals none
     * @param bypassType damage type of the armor-piercing part, null if the turret deals none
     */
    private record Spec(int argb, int duration, boolean lightning, @Nullable SoundEvent hitSound,
                        @Nullable ResourceKey<DamageType> normalType, @Nullable ResourceKey<DamageType> bypassType) {
    }

    /** Colours from 1.12.2 {@code ColorOM(r, g, b, a)}; the arc lightning (no 1.12.2 colour) is light blue. */
    private static final Map<TurretType, Spec> SPECS = Map.of(
            TurretType.LASER_TURRET, new Spec(argb(1F, 0.1F, 0F, 0.38F), 5, false, ModSounds.LASER_HIT,
                    ModDamageTypes.LASER, null),
            TurretType.RAILGUN_TURRET, new Spec(argb(1F, 0.5F, 0F, 0.2F), 3, false, ModSounds.RAIL_GUN_HIT,
                    null, ModDamageTypes.RAILGUN),
            TurretType.ARC_TURRET, new Spec(argb(0.6F, 0.8F, 1F, 0.6F), 3, true, null,
                    ModDamageTypes.ARC, ModDamageTypes.ARC_ARMOR_BYPASS));

    private RayWeapons() {
    }

    private static int argb(float r, float g, float b, float a) {
        return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    private static int armor(Entity entity) {
        return entity instanceof LivingEntity living ? living.getArmorValue() : 0;
    }

    /**
     * 1.12.2 {@code doTargetedShot} + {@code shootRay}: one ray per scattershot level towards the target's eyes,
     * scattered by the turret's accuracy (more for small or far targets). The first damageable entity on the ray
     * before the first solid block is hit.
     */
    public static void shoot(TurretBlockEntity turret, TurretBaseBlockEntity base, Entity target, Vec3 origin, Vec3 renderOrigin) {
        ServerLevel level = (ServerLevel) turret.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        TurretType type = turret.getTurretType();
        Spec spec = SPECS.get(type);
        TurretBaseLogic logic = base.getLogic();
        double accuracy = logic.getAccuracyDeviation(type);
        Vec3 aim = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        for (int i = 0; i <= logic.getScattershotCount(type); i++) {
            double scale = ProjectileMath.raySpread(accuracy, target.getBbHeight(), aim.distanceTo(origin), logic.getRange(type));
            Vec3 end = aim.add(level.random.nextGaussian() * scale, level.random.nextGaussian() * scale,
                    level.random.nextGaussian() * scale);
            Vec3 start = origin.add(end.subtract(origin).normalize().scale(0.8D));
            playSound(level, turret.getBlockPos(), TurretWeapon.of(type).launchSound());

            BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, target));
            double blockRange = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation().distanceTo(start) : NO_BLOCK_HIT;

            Entity hit = null;
            for (Entity entity : entitiesOnRay(level, start, end)) {
                if (entity.getBoundingBox().clip(start, end).map(p -> p.distanceTo(start) <= blockRange).orElse(true)
                        && hitEntity(level, turret, base, spec, entity, type == TurretType.ARC_TURRET)) {
                    ray(level, spec, type == TurretType.ARC_TURRET ? renderOrigin : start, end);
                    hit = entity;
                    break;
                }
            }
            if (hit == null) {
                if (blockHit.getType() == HitResult.Type.BLOCK) {
                    ray(level, spec, start, blockHit.getLocation());
                    handleBlockHit(level, type, blockHit.getBlockPos());
                } else {
                    // 1.12.2 drew the ray three times as long when it hit nothing
                    ray(level, spec, start, end.add(end.subtract(start).scale(2D)));
                }
            }
            RayShotCallback.EVENT.invoker().onRayShot(turret, start, end, hit);
        }
    }

    /** Entities whose bounding box crosses the segment, nearest first (1.12.2 {@code WorldUtil.traceEntities}). */
    private static List<Entity> entitiesOnRay(ServerLevel level, Vec3 start, Vec3 end) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : level.getEntities((Entity) null, new AABB(start, end).inflate(1.0D),
                e -> e.isPickable() && !e.isSpectator())) {
            if (entity.getBoundingBox().clip(start, end).isPresent()) result.add(entity);
        }
        result.sort(Comparator.comparingDouble(e -> e.getBoundingBox().clip(start, end).map(p -> p.distanceToSqr(start)).orElse(0D)));
        return result;
    }

    /**
     * 1.12.2 {@code onHitEntity}: damage, hit effects and hit sound if the entity may be damaged.
     *
     * @param arc whether the hit starts an arc chain
     */
    private static boolean hitEntity(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Spec spec,
                                     Entity entity, boolean arc) {
        if (!TurretDamage.canDamage(level, base.getBlockPos(), entity)) return false;
        damage(level, turret, base, spec, entity, RayMath.damageModifier(turret.getTurretType(), armor(entity)));
        if (arc) {
            shootArcChain(level, base, turret, spec, entity, RayMath.ARC_HOPS, new ArrayList<>());
        }
        if (spec.hitSound() != null) {
            level.playSound(null, entity.blockPosition(), spec.hitSound(), SoundSource.AMBIENT,
                    (float) OMTConfig.get().turrets.turretSoundVolume, level.random.nextFloat() + 0.5F);
        }
        return true;
    }

    /**
     * 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: each hit uses one hop; the arc jumps to the first damageable
     * living entity within {@link RayMath#arcJumpRange} that was not hit yet. With two hops the arc reaches one
     * extra entity (the second jump has range 0), which takes {@link RayMath#ARC_JUMP_FACTOR} of the damage.
     */
    private static void shootArcChain(ServerLevel level, TurretBaseBlockEntity base, TurretBlockEntity turret, Spec spec,
                                      Entity from, int hops, List<Integer> hitIds) {
        if (hops <= 0) return;
        hitIds.add(from.getId());
        double range = RayMath.arcJumpRange(hops);
        int remaining = hops - 1;
        for (LivingEntity next : level.getEntitiesOfClass(LivingEntity.class, new AABB(from.position(), from.position()).inflate(range))) {
            if (!hitIds.contains(next.getId()) && TurretDamage.canDamage(level, base.getBlockPos(), next)) {
                // 1.12.2 order: the chain continues from the next target before it is damaged
                shootArcChain(level, base, turret, spec, next, remaining, hitIds);
                damage(level, turret, base, spec, next, RayMath.ARC_JUMP_FACTOR);
                double eye = from.getEyeHeight();
                ray(level, spec, from.position().add(0, eye, 0), next.position().add(0, eye, 0));
                break;
            }
        }
    }

    /** Deals {@link RayMath#damage}, split into a normal and an armor-piercing part ({@link RayMath#split}). */
    private static void damage(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Spec spec,
                               Entity entity, double modifier) {
        TurretBaseLogic logic = base.getLogic();
        TurretType type = turret.getTurretType();
        float health = entity instanceof LivingEntity living ? living.getHealth() : 0F;
        float damage = RayMath.damage(OMTConfig.get().turrets.get(type).baseDamage, modifier,
                logic.getDamageAmpBonus(type), health);
        ProjectileMath.Split split = RayMath.split(type, damage);
        int looting = logic.getLootingLevel(VersionCompat.maxLootingLevel(level));
        if (spec.normalType() != null) {
            TurretDamage.hurt(level, entity, spec.normalType(), split.normal(), null, base.getBlockPos(), looting);
        }
        if (spec.bypassType() != null) {
            TurretDamage.hurt(level, entity, spec.bypassType(), split.armorBypass(), null, base.getBlockPos(), looting);
        }
    }

    /**
     * 1.12.2 {@code RailGunTurretTileEntity.handleBlockHit}: with {@code canRailgunDestroyBlocks} the railgun breaks
     * the hit block (no drops) if its hardness is below 200. Unbreakable blocks (hardness -1) are kept; 1.12.2
     * would have destroyed them too.
     */
    private static void handleBlockHit(ServerLevel level, TurretType type, BlockPos pos) {
        if (type != TurretType.RAILGUN_TURRET || !OMTConfig.get().turrets.canRailgunDestroyBlocks) return;
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof TurretBlock) return;
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness >= 0 && hardness < 200) {
            level.destroyBlock(pos, false);
        }
    }

    private static void ray(ServerLevel level, Spec spec, Vec3 from, Vec3 to) {
        RayPacket.send(level, new RayPacket(from, to, spec.argb(), spec.duration(), spec.lightning()));
    }

    private static void playSound(ServerLevel level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, (float) OMTConfig.get().turrets.turretSoundVolume,
                level.random.nextFloat() + 0.5F);
    }
}

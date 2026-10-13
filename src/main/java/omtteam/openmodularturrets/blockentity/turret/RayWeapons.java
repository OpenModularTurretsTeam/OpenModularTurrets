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
import java.util.function.ToDoubleFunction;

/**
 * Instant-hit ray weapons (1.12.2 {@code RayTracingTurret} with {@code LaserTurretTileEntity},
 * {@code RailGunTurretTileEntity} and {@code ArcTurretTileEntity}).
 */
public final class RayWeapons {
    /** 1.12.2 {@code blockRange} when nothing was hit. */
    private static final double NO_BLOCK_HIT = 500;
    /** 1.12.2 {@code ArcTurretTileEntity}: {@code hop = 2} on launch, one jump per hit. */
    private static final int ARC_HOPS = 2;
    /**
     * 1.12.2 arc damage modifier {@code 1 / (3 - hop)}: the directly hit entity is damaged at hop 2 (full damage), the
     * jump target only after the chain reached hop 0 (a third).
     */
    private static final double ARC_JUMP_FACTOR = 1D / 3D;

    /**
     * Per-turret ray behaviour.
     *
     * @param damageModifier 1.12.2 {@code getDamageModifier(entity)} for the directly hit entity
     * @param normalFactor   share of the damage dealt as normal damage
     * @param bypassFactor   share dealt as armor-piercing damage
     */
    private record Spec(int argb, int duration, boolean lightning, @Nullable SoundEvent hitSound,
                        ToDoubleFunction<Entity> damageModifier, float normalFactor, float bypassFactor,
                        @Nullable ResourceKey<DamageType> normalType, @Nullable ResourceKey<DamageType> bypassType) {
    }

    /** Colours from 1.12.2 {@code ColorOM(r, g, b, a)}; the arc lightning (no 1.12.2 colour) is light blue. */
    private static final Map<TurretType, Spec> SPECS = Map.of(
            // (30 - armor) / 20 + 0.1 -> 0.6x to 1.6x, normal damage only
            TurretType.LASER_TURRET, new Spec(argb(1F, 0.1F, 0F, 0.38F), 5, false, ModSounds.LASER_HIT,
                    entity -> (30 - armor(entity)) / 20F + 0.1F, 1F, 0F, ModDamageTypes.LASER, null),
            // armor / 20 + 0.6, armor-piercing only
            TurretType.RAILGUN_TURRET, new Spec(argb(1F, 0.5F, 0F, 0.2F), 3, false, ModSounds.RAIL_GUN_HIT,
                    entity -> armor(entity) / 20F + 0.6F, 0F, 1F, null, ModDamageTypes.RAILGUN),
            TurretType.ARC_TURRET, new Spec(argb(0.6F, 0.8F, 1F, 0.6F), 3, true, null,
                    entity -> 1F, 0.6F, 0.4F, ModDamageTypes.ARC, ModDamageTypes.ARC_ARMOR_BYPASS));

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
        TurretType type = turret.getTurretType();
        Spec spec = SPECS.get(type);
        TurretBaseLogic logic = base.getLogic();
        double accuracy = logic.getAccuracyDeviation(type);
        Vec3 aim = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        for (int i = 0; i <= logic.getScattershotCount(); i++) {
            // 1.12.2 deviationModifier: small targets and far targets scatter more
            double deviationModifier = 10D * (target.getBbHeight() < 0.5 ? 1.5D : 1D)
                    * (aim.distanceTo(origin) * 0.2D / logic.getRange(type) + 0.3D);
            double scale = 0.003D * deviationModifier * accuracy;
            Vec3 end = aim.add(level.random.nextGaussian() * scale, level.random.nextGaussian() * scale,
                    level.random.nextGaussian() * scale);
            Vec3 start = origin.add(end.subtract(origin).normalize().scale(0.8D));
            playSound(level, turret.getBlockPos(), TurretWeapon.of(type).launchSound());

            BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, target));
            double blockRange = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation().distanceTo(start) : NO_BLOCK_HIT;

            boolean hit = false;
            for (Entity entity : entitiesOnRay(level, start, end)) {
                if (entity.getBoundingBox().clip(start, end).map(p -> p.distanceTo(start) <= blockRange).orElse(true)
                        && hitEntity(level, turret, base, spec, entity, type == TurretType.ARC_TURRET)) {
                    ray(level, spec, type == TurretType.ARC_TURRET ? renderOrigin : start, end);
                    hit = true;
                    break;
                }
            }
            if (!hit) {
                if (blockHit.getType() == HitResult.Type.BLOCK) {
                    ray(level, spec, start, blockHit.getLocation());
                    handleBlockHit(level, type, blockHit.getBlockPos());
                } else {
                    // 1.12.2 drew the ray three times as long when it hit nothing
                    ray(level, spec, start, end.add(end.subtract(start).scale(2D)));
                }
            }
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
        damage(level, turret, base, spec, entity, spec.damageModifier().applyAsDouble(entity));
        if (arc) {
            shootArcChain(level, base, turret, spec, entity, ARC_HOPS, new ArrayList<>());
        }
        if (spec.hitSound() != null) {
            level.playSound(null, entity.blockPosition(), spec.hitSound(), SoundSource.AMBIENT,
                    (float) OMTConfig.get().turrets.turretSoundVolume, level.random.nextFloat() + 0.5F);
        }
        return true;
    }

    /**
     * 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: each hit uses one hop; the arc jumps to the first damageable
     * living entity within {@code 3 * remaining hops} blocks that was not hit yet. With two hops the arc reaches one
     * extra entity (the second jump has range 0), which takes {@link #ARC_JUMP_FACTOR} of the damage.
     */
    private static void shootArcChain(ServerLevel level, TurretBaseBlockEntity base, TurretBlockEntity turret, Spec spec,
                                      Entity from, int hops, List<Integer> hitIds) {
        if (hops <= 0) return;
        int remaining = hops - 1;
        hitIds.add(from.getId());
        double range = 3 * remaining;
        for (LivingEntity next : level.getEntitiesOfClass(LivingEntity.class, new AABB(from.position(), from.position()).inflate(range))) {
            if (!hitIds.contains(next.getId()) && TurretDamage.canDamage(level, base.getBlockPos(), next)) {
                // 1.12.2 order: the chain continues from the next target before it is damaged
                shootArcChain(level, base, turret, spec, next, remaining, hitIds);
                damage(level, turret, base, spec, next, ARC_JUMP_FACTOR);
                double eye = from.getEyeHeight();
                ray(level, spec, from.position().add(0, eye, 0), next.position().add(0, eye, 0));
                break;
            }
        }
    }

    /**
     * 1.12.2 {@code RayTracingTurret.damageEntity}: base damage times the modifier, plus the damage amp bonus,
     * split into a normal and an armor-piercing part.
     */
    private static void damage(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base, Spec spec,
                               Entity entity, double modifier) {
        TurretBaseLogic logic = base.getLogic();
        TurretType type = turret.getTurretType();
        float damage = (float) (OMTConfig.get().turrets.get(type).baseDamage * modifier);
        double amp = logic.getDamageAmpBonus(type);
        if (amp > 0 && entity instanceof LivingEntity living) {
            damage += (float) ((int) living.getHealth() * amp);
        }
        int looting = logic.getLootingLevel(VersionCompat.maxLootingLevel(level));
        if (spec.normalType() != null) {
            TurretDamage.hurt(level, entity, spec.normalType(), damage * spec.normalFactor(), null, base.getBlockPos(), looting);
        }
        if (spec.bypassType() != null) {
            TurretDamage.hurt(level, entity, spec.bypassType(), damage * spec.bypassFactor(), null, base.getBlockPos(), looting);
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

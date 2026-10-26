package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.RayMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.network.RayPacket;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Shoots {@link RayWeapon}s (1.12.2 {@code RayTracingTurret} with {@code LaserTurretTileEntity},
 * {@code RailGunTurretTileEntity} and {@code ArcTurretTileEntity}). Visuals and damage types come from the weapon,
 * the damage from {@link RayMath}.
 */
public final class RayWeapons {
    /** 1.12.2 {@code blockRange} when nothing was hit. */
    private static final double NO_BLOCK_HIT = 500;

    private RayWeapons() {
    }

    private static int armor(Entity entity) {
        return entity instanceof LivingEntity living ? living.getArmorValue() : 0;
    }

    /**
     * 1.12.2 {@code doTargetedShot} + {@code shootRay}: one ray per scattershot level towards the target's eyes,
     * scattered by the turret's accuracy (more for small or far targets). The first damageable entity on the ray
     * before the first solid block is hit.
     *
     * @param renderOrigin where a hit is drawn from if the weapon {@linkplain RayWeapon#drawFromTip() draws from the tip}
     */
    public static void shoot(TurretBlockEntity turret, TurretBaseBlockEntity base, RayWeapon weapon, Entity target,
                             Vec3 origin, Vec3 renderOrigin) {
        if (!(turret.getLevel() instanceof ServerLevel level)) {
            return;
        }
        TurretType type = turret.getTurretType();
        TurretBaseLogic logic = base.getLogic();
        double accuracy = logic.getAccuracyDeviation(type);
        Vec3 aim = new Vec3(target.getX(), target.getEyeY(), target.getZ());
        for (int i = 0; i <= logic.getScattershotCount(type); i++) {
            double scale = ProjectileMath.raySpread(accuracy, target.getBbHeight(), aim.distanceTo(origin), logic.getRange(type));
            Vec3 end = aim.add(level.random.nextGaussian() * scale, level.random.nextGaussian() * scale,
                    level.random.nextGaussian() * scale);
            Vec3 start = origin.add(end.subtract(origin).normalize().scale(0.8D));
            playSound(level, turret.getBlockPos(), weapon.launchSound());

            BlockHitResult blockHit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, target));
            double blockRange = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation().distanceTo(start) : NO_BLOCK_HIT;

            Entity hit = null;
            for (Entity entity : entitiesOnRay(level, start, end)) {
                if (entity.getBoundingBox().clip(start, end).map(p -> p.distanceTo(start) <= blockRange).orElse(true)
                        && hitEntity(level, turret, base, weapon, entity)) {
                    ray(level, weapon, weapon.drawFromTip() ? renderOrigin : start, end);
                    hit = entity;
                    break;
                }
            }
            if (hit == null) {
                if (blockHit.getType() == HitResult.Type.BLOCK) {
                    ray(level, weapon, start, blockHit.getLocation());
                    if (weapon.blockHit() != null) weapon.blockHit().onBlockHit(level, blockHit.getBlockPos());
                } else {
                    // 1.12.2 drew the ray three times as long when it hit nothing
                    ray(level, weapon, start, end.add(end.subtract(start).scale(2D)));
                }
            }
            RayShotCallback.fire(turret, start, end, hit);
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

    /** 1.12.2 {@code onHitEntity}: damage, hit effects and hit sound if the entity may be damaged. */
    private static boolean hitEntity(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base,
                                     RayWeapon weapon, Entity entity) {
        if (!TurretDamage.canDamage(level, base.getBlockPos(), entity)) return false;
        damage(level, turret, base, weapon, entity, RayMath.damageModifier(weapon.profile(), armor(entity)));
        if (weapon.profile().arcChain()) {
            shootArcChain(level, base, turret, weapon, entity, RayMath.ARC_HOPS, new ArrayList<>());
        }
        if (weapon.hitSound() != null) {
            level.playSound(null, entity.blockPosition(), weapon.hitSound(), SoundSource.AMBIENT,
                    (float) OMTConfig.get().turrets.turretSoundVolume, level.random.nextFloat() + 0.5F);
        }
        return true;
    }

    /**
     * 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: each hit uses one hop; the arc jumps to the first damageable
     * living entity within {@link RayMath#arcJumpRange} that was not hit yet. With two hops the arc reaches one
     * extra entity (the second jump has range 0), which takes {@link RayMath#ARC_JUMP_FACTOR} of the damage.
     */
    private static void shootArcChain(ServerLevel level, TurretBaseBlockEntity base, TurretBlockEntity turret,
                                      RayWeapon weapon, Entity from, int hops, List<Integer> hitIds) {
        if (hops <= 0) return;
        hitIds.add(from.getId());
        double range = RayMath.arcJumpRange(hops);
        int remaining = hops - 1;
        for (LivingEntity next : level.getEntitiesOfClass(LivingEntity.class, new AABB(from.position(), from.position()).inflate(range))) {
            if (!hitIds.contains(next.getId()) && TurretDamage.canDamage(level, base.getBlockPos(), next)) {
                // 1.12.2 order: the chain continues from the next target before it is damaged
                shootArcChain(level, base, turret, weapon, next, remaining, hitIds);
                damage(level, turret, base, weapon, next, RayMath.ARC_JUMP_FACTOR);
                double eye = from.getEyeHeight();
                ray(level, weapon, from.position().add(0, eye, 0), next.position().add(0, eye, 0));
                break;
            }
        }
    }

    /**
     * Deals {@link RayMath#damage}, split into a normal and an armor-piercing part ({@link RayMath#split}), then calls
     * the weapon's {@linkplain RayWeapon#entityHit() entity hit handler}.
     */
    private static void damage(ServerLevel level, TurretBlockEntity turret, TurretBaseBlockEntity base,
                               RayWeapon weapon, Entity entity, double modifier) {
        TurretBaseLogic logic = base.getLogic();
        TurretType type = turret.getTurretType();
        float health = entity instanceof LivingEntity living ? living.getHealth() : 0F;
        float damage = RayMath.damage(OMTConfig.get().turrets.get(type).baseDamage, modifier,
                logic.getDamageAmpBonus(type), health);
        ProjectileMath.Split split = RayMath.split(weapon.profile(), damage);
        int looting = logic.getLootingLevel(VersionCompat.maxLootingLevel(level));
        if (weapon.normalDamage() != null) {
            TurretDamage.hurt(level, entity, weapon.normalDamage(), split.normal(), null, base.getBlockPos(), looting);
        }
        if (weapon.bypassDamage() != null) {
            TurretDamage.hurt(level, entity, weapon.bypassDamage(), split.armorBypass(), null, base.getBlockPos(), looting);
        }
        if (weapon.entityHit() != null) {
            weapon.entityHit().onEntityHit(level, turret, base, entity);
        }
    }

    /**
     * 1.12.2 {@code RailGunTurretTileEntity.handleBlockHit}: with {@code canRailgunDestroyBlocks} the railgun breaks
     * the hit block (no drops) if its hardness is below 200. Unbreakable blocks (hardness -1) are kept; 1.12.2
     * would have destroyed them too.
     */
    public static void railgunBlockHit(ServerLevel level, BlockPos pos) {
        if (!OMTConfig.get().turrets.canRailgunDestroyBlocks) return;
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof TurretBlock) return;
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness >= 0 && hardness < 200) {
            level.destroyBlock(pos, false);
        }
    }

    private static void ray(ServerLevel level, RayWeapon weapon, Vec3 from, Vec3 to) {
        RayPacket.send(level, new RayPacket(from, to, weapon.argb(), weapon.duration(), weapon.lightning()));
    }

    private static void playSound(ServerLevel level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, (float) OMTConfig.get().turrets.turretSoundVolume,
                level.random.nextFloat() + 0.5F);
    }
}

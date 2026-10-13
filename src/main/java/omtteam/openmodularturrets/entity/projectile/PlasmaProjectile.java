package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Plasma ball (1.12.2 {@code PlasmaProjectile}): lives 30 ticks and bursts on impact, dealing half normal and half
 * armor-piercing damage within 2 blocks.
 */
public class PlasmaProjectile extends TurretProjectile {
    private static final int MAX_AGE = 30;
    private static final double RADIUS = 2;

    public PlasmaProjectile(EntityType<? extends PlasmaProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.PLASMA_TURRET;
    }

    /** 1.12.2 used 0.01 on the server (and 0.001 on the client by mistake). */
    @Override
    protected float getDropPerTick() {
        return 0.01F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isRemoved() && !level().isClientSide() && tickCount > MAX_AGE) {
            discard();
        }
    }

    private void burst() {
        Vec3 motion = getDeltaMovement();
        setPos(getX() + motion.x * 0.8, getY() + motion.y * 0.8, getZ() + motion.z * 0.8);
        setDeltaMovement(Vec3.ZERO);
        // 1.12.2 MessageSpawnParticleQuad: 15 flame and 15 large smoke particles in a 2x1x2 area, speed 0.2
        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, getX(), getY(), getZ(), 15, 1.0D, 0.5D, 1.0D, 0.2D);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 15, 1.0D, 0.5D, 1.0D, 0.2D);
        }
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(RADIUS))) {
            if (canDamage(target)) {
                int damage = damageWithAmp(baseDamage(), target);
                hurt(target, ModDamageTypes.PLASMA, damage * 0.5F);
                hurt(target, ModDamageTypes.PLASMA_ARMOR_BYPASS, damage * 0.5F);
            }
        }
        discard();
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (canDamage(entity)) burst();
    }

    /** 1.12.2 only burst when the block corner was within ~0.7 blocks (distanceSq ≤ 0.5) of the projectile. */
    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        if (pos.distToLowCornerSqr(getX(), getY(), getZ()) <= 0.5D) {
            burst();
        }
    }

    /** 1.12.2 {@code isImmuneToExplosions}. */
    @Override
    protected boolean isImmuneToExplosions() {
        return true;
    }
}

package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import org.jetbrains.annotations.Nullable;

/**
 * Rocket (1.12.2 {@code RocketProjectile}): explodes on impact, damaging everything within 5 blocks. Homes in on its
 * target if {@code canRocketsHome} is set.
 */
public class RocketProjectile extends TurretProjectile {
    private static final double RADIUS = 5;
    private static final float HOMING_SPEED = 0.24F;

    @Nullable
    private Entity target;

    public RocketProjectile(EntityType<? extends RocketProjectile> type, Level level) {
        super(type, level);
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.ROCKET_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.0F;
    }

    /**
     * 1.12.2 {@code onEntityUpdate}: with homing enabled the rocket steers towards its target every tick and is
     * removed when the target is gone. (In 1.12.2 this code was never reached because {@code onUpdate} did not call
     * it.)
     */
    @Override
    public void tick() {
        if (!level().isClientSide() && OMTConfig.get().turrets.canRocketsHome) {
            if (target == null || !target.isAlive()) {
                discard();
                return;
            }
            shoot(target.getX() - getX(), target.getY() + target.getEyeHeight() - 1.1F - getY(),
                    target.getZ() - getZ(), HOMING_SPEED, 0.0F);
        }
        if (level().isClientSide()) {
            // 1.12.2 onEntityUpdate: 21 smoke particles per tick around the rocket (never reached in 1.12.2)
            for (int i = 0; i <= 20; i++) {
                level().addParticle(ParticleTypes.SMOKE, getX() + random.nextGaussian() / 10, getY() + random.nextGaussian() / 10,
                        getZ() + random.nextGaussian() / 10, 0, 0, 0);
            }
        }
        super.tick();
    }

    private void explode() {
        boolean destroyBlocks = OMTConfig.get().turrets.canRocketsDestroyBlocks;
        level().explode(null, getX(), getY(), getZ(), destroyBlocks ? 2.3F : 0.1F,
                destroyBlocks ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(RADIUS))) {
            int damage = damageWithAmp(baseDamage(), entity);
            if (OMTConfig.get().turrets.canRocketsHurtEnderDragon && entity instanceof EnderDragon) {
                // the dragon ignores most damage sources, so its health is reduced directly
                entity.setHealth(entity.getHealth() - damage);
                entity.invulnerableTime = 0;
            } else if (canDamage(entity)) {
                hurt(entity, ModDamageTypes.ROCKET, damage);
            }
        }
        discard();
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (canDamage(entity)) explode();
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        explode();
    }
}

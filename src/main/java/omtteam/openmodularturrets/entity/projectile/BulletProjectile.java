package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModSounds;

/**
 * Machine gun bullet (oldmod {@code BulletProjectile}): flies straight, damages the first entity it touches.
 */
public class BulletProjectile extends TurretProjectile {
    public BulletProjectile(EntityType<? extends BulletProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.MACHINE_GUN_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.0F;
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        hurt(entity, ModDamageTypes.BULLET, damageWithAmp(baseDamage(), entity));
        discard();
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        level().playSound(null, blockPosition(), ModSounds.BULLET_HIT, SoundSource.AMBIENT,
                (float) OMTConfig.get().turrets.turretSoundVolume, random.nextFloat() + 0.5F);
        discard();
    }
}

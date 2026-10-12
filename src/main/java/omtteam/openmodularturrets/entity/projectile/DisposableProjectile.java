package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.inventory.AmmoRules;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Disposable item turret ammo (oldmod {@code DisposableTurretProjectile}): the fired item adds its allow-list damage
 * bonus.
 */
public class DisposableProjectile extends ItemCarryingProjectile {
    public DisposableProjectile(EntityType<? extends DisposableProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.DISPOSABLE_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.03F;
    }

    /** oldmod: the shown item had a 40 tick lifespan, the same as the projectile. */
    @Override
    protected boolean removeItemWithProjectile() {
        return true;
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        int damage = baseDamage() + AmmoRules.getDisposableAmmoDamageBonus(getItem());
        hurt(entity, ModDamageTypes.DISPOSABLE, damageWithAmp(damage, entity));
        discard();
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        discard();
    }
}

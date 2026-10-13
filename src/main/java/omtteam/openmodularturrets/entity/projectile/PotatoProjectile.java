package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Potato cannon ammo (1.12.2 {@code PotatoProjectile}).
 */
public class PotatoProjectile extends ItemCarryingProjectile {
    public PotatoProjectile(EntityType<? extends PotatoProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.POTATO_CANNON_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.03F;
    }

    /** As in 1.12.2, the potato item lives on (unpickable) if the projectile expires without hitting anything. */
    @Override
    protected boolean removeItemWithProjectile() {
        return false;
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        hurt(entity, ModDamageTypes.POTATO, damageWithAmp(baseDamage(), entity));
        removeItem();
        discard();
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        removeItem();
        discard();
    }
}

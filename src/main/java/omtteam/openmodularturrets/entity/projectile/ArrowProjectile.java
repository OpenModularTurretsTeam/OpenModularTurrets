package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * Crossbow turret bolt (shown as an arrow)
 */
public class ArrowProjectile extends TurretProjectile {
    public ArrowProjectile(EntityType<? extends ArrowProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.CROSSBOW_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.03F;
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        hurt(entity, ModDamageTypes.ARROW, damageWithAmp(baseDamage(), entity));
        discard();
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        discard();
    }
}

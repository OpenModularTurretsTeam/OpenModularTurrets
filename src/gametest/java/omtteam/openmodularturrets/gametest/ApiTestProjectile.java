package omtteam.openmodularturrets.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModDamageTypes;

/**
 * The projectile of {@link ApiTestTurrets#API_PROJECTILE_TURRET}, written the way another mod would write one: it
 * extends {@link TurretProjectile} outside of Open Modular Turrets and has its own entity type
 * ({@link ApiTestEntities#PROJECTILE}). Flies straight and deals the turret's base damage on a hit.
 */
public class ApiTestProjectile extends TurretProjectile {
    public ApiTestProjectile(EntityType<? extends ApiTestProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected TurretType getTurretType() {
        return ApiTestTurrets.API_PROJECTILE_TURRET;
    }

    @Override
    public float getGravityEffect() {
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
        discard();
    }
}

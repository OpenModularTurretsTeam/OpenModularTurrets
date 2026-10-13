package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.openmodularturrets.compat.VersionCompat;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModItems;

/**
 * Incendiary turret ammo
 */
public class BlazingClayProjectile extends TurretProjectile implements ItemSupplier {
    private static final double RADIUS = 5;
    private static final int FIRE_SECONDS = 5;

    public BlazingClayProjectile(EntityType<? extends BlazingClayProjectile> type, Level level) {
        super(type, level);
    }

    /** Shown as the ammo item (1.12.2 {@code RenderSnowball}). */
    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.AMMO_BLAZING_CLAY);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.INCENDIARY_TURRET;
    }

    @Override
    protected float getDropPerTick() {
        return 0.0F;
    }

    /** The damage amp bonus is computed from the directly hit entity and applied to all. */
    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        burnArea(damageWithAmp(baseDamage(), entity));
    }

    /** Hitting a block deals the base damage without amp bonus. */
    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        burnArea(baseDamage());
    }

    private void burnArea(int damage) {
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(RADIUS))) {
            if (canDamage(target)) {
                hurt(target, ModDamageTypes.BLAZING_CLAY, damage);
                VersionCompat.setOnFire(target, FIRE_SECONDS);
            }
        }
        discard();
    }
}

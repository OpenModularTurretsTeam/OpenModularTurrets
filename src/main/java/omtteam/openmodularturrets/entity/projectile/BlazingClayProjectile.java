package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModItems;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull ItemStack getItem() {
        return new ItemStack(ModItems.AMMO_BLAZING_CLAY);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.INCENDIARY_TURRET;
    }

    @Override
    public float getGravityEffect() {
        return 0.0F;
    }

    /**
     * The damage amp bonus is computed from the directly hit entity and applied to all; the range falloff is taken at
     * the projectile (the fire time is not scaled).
     */
    @Override
    protected void onHitEntity(Entity entity) {
        if (!canDamage(entity)) return;
        burnArea(ampDamage(baseDamage(), entity) * damageFactor());
    }

    /** Hitting a block deals the base damage without amp bonus (with the range falloff). */
    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        burnArea(baseDamage() * damageFactor());
    }

    private void burnArea(float damage) {
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(RADIUS))) {
            if (canDamage(target)) {
                hurt(target, ModDamageTypes.BLAZING_CLAY, damage);
                VersionCompat.setOnFire(target, FIRE_SECONDS);
            }
        }
        discard();
    }
}

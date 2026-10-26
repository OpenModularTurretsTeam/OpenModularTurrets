package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.ModDamageTypes;
import omtteam.openmodularturrets.registry.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * Grenade: stops on blocks, bounces up off entities and explodes after some time,
 * dealing 90 % normal and 10 % armor-piercing damage within 3 blocks.
 */
public class GrenadeProjectile extends TurretProjectile implements ItemSupplier {
    private static final int FUSE_TICKS = 29;
    private static final int TICKS_AFTER_ENTITY_HIT = 25;
    private static final double RADIUS = 3;
    private boolean hitEntity;

    public GrenadeProjectile(EntityType<? extends GrenadeProjectile> type, Level level) {
        super(type, level);
    }

    /** Shown as the ammo item  */
    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(ModItems.AMMO_GRENADE);
    }

    @Override
    protected TurretType getTurretType() {
        return TurretType.GRENADE_TURRET;
    }

    @Override
    public float getGravityEffect() {
        return 0.03F;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isRemoved() && !level().isClientSide() && tickCount >= FUSE_TICKS) {
            explode();
        }
    }

    private void explode() {
        boolean destroyBlocks = OMTConfig.get().turrets.canGrenadesDestroyBlocks;
        explodeHere(destroyBlocks ? 1.4F : 0.1F, destroyBlocks ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(RADIUS))) {
            if (canDamage(target)) {
                int damage = damageWithAmp(baseDamage(), target);
                ProjectileMath.Split split = ProjectileMath.grenadeSplit(damage);
                hurt(target, ModDamageTypes.GRENADE, split.normal());
                hurt(target, ModDamageTypes.GRENADE_ARMOR_BYPASS, split.armorBypass());
            }
        }
        discard();
    }

    @Override
    protected void onHitEntity(Entity entity) {
        if (hitEntity || !canDamage(entity)) return;
        Vec3 motion = getDeltaMovement();
        setDeltaMovement(motion.x * 0.2D, motion.y * 1.2D, motion.z * 0.2D);
        hasImpulse = true;
        hitEntity = true;
        tickCount = TICKS_AFTER_ENTITY_HIT;
    }

    @Override
    protected void onHitBlock(BlockState state, BlockPos pos) {
        setDeltaMovement(Vec3.ZERO);
    }

    @Override
    protected boolean isImmuneToExplosions() {
        return true;
    }
}

package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.omlib.compat.VersionCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Vector3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
//? if >=1.21 {
import net.minecraft.network.syncher.SynchedEntityData;
//?}


/**
 * Base class of all turret projectiles. Ported from 1.12.2 {@code TurretProjectile}: custom straight-line movement
 * with optional gravity, a 40 tick lifetime, hits on any pickable entity near the flight path and on solid blocks.
 * Projectiles are never saved, and remove themselves instead of flying into a chunk that does not tick entities.
 */
public abstract class TurretProjectile extends Projectile {
    @Nullable
    private BlockPos basePos;
    private int ampLevel;
    /** Captured when fired, like the 1.12.2 {@code fakeDrops}; without a base: no loot. */
    private int lootingLevel = TurretBaseLogic.NO_LOOT;
    /** From the weapon that fired the projectile (see {@link ProjectileWeapon#onHit}). */
    @Nullable
    private ProjectileWeapon.HitHandler hitHandler;
    /** Ids of the entities {@link #hitHandler} was called for. */
    @Nullable
    private Set<Integer> notifiedHits;

    protected TurretProjectile(EntityType<? extends TurretProjectile> type, Level level) {
        super(type, level);
    }

    /**
     * Links the projectile to the base that fired it (trust checks, kill counting) and copies the base's damage amp
     * level (as in the 1.12.2 constructor).
     */
    public void setBase(TurretBaseBlockEntity base) {
        this.basePos = base.getBlockPos();
        this.ampLevel = base.getLogic().getDamageAmpLevel();
        this.lootingLevel = base.getLogic().getLootingLevel(VersionCompat.maxLootingLevel(level()));
    }

    /** Set by the turret that fires the projectile, from its {@link ProjectileWeapon}. */
    public void setHitHandler(@Nullable ProjectileWeapon.HitHandler hitHandler) {
        this.hitHandler = hitHandler;
    }

    /** The turret type whose config (damage, damage amp) applies. */
    protected abstract TurretType getTurretType();

    /**
     * Gravity effect of this projectile: the drop per tick of flight time, i.e. how much its downward motion grows
     * with every tick it flies (blocks per tick, 1.12.2 {@code gravity}). The turret raises its aim by the drop
     * accumulated over the flight time to the target.
     */
    public abstract float getGravityEffect();

    /** Called on the server when the flight path touches a pickable entity. */
    protected abstract void onHitEntity(Entity entity);

    /**
     * Called on the server when the projectile hits a block with a collision shape (1.12.2: a solid material),
     * other than a turret.
     */
    protected abstract void onHitBlock(BlockState state, BlockPos pos);

    // ---- Movement ----

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && tickCount > ProjectileMath.MAX_AGE) {
            discard();
            return;
        }
        Vec3 position = position();
        Vec3 motion = getDeltaMovement();

        if (!level().isClientSide()) {
            AABB sweep = getBoundingBox().expandTowards(motion.scale(1.2D));
            for (Entity entity : level().getEntities(this, sweep, e -> e.isPickable() && !e.isSpectator())) {
                onHitEntity(entity);
                if (isRemoved()) return;
            }
            BlockHitResult hit = level().clip(new ClipContext(position, position.add(motion), ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockState state = level().getBlockState(hit.getBlockPos());
                // projectiles fly through turrets (1.12.2: BlockAbstractTurretHead is ignored)
                if (!(state.getBlock() instanceof TurretBlock)) {
                    onHitBlock(state, hit.getBlockPos());
                    if (isRemoved()) return;
                }
            }
        }

        Vec3 nextPosition = position.add(motion);
        if (level() instanceof ServerLevel serverLevel
                && !serverLevel.isPositionEntityTicking(BlockPos.containing(nextPosition))) {
            // 1.12.2: a projectile flying into an unloaded chunk removed itself; here also into a loaded chunk that
            // does not tick entities, where it would freeze instead of landing or expiring
            discard();
            return;
        }
        setPos(nextPosition);
        // need this for applying hit effects
        motion = getDeltaMovement();

        updateRotation();
        if (isInWater() && level().isClientSide()) {
            // Four bubbles per tick behind the projectile while in water
            for (int i = 0; i < 4; i++) {
                level().addParticle(ParticleTypes.BUBBLE, getX() - motion.x * 0.25D, getY() - motion.y * 0.25D,
                        getZ() - motion.z * 0.25D, motion.x, motion.y, motion.z);
            }
        }
        Vector3 next = ProjectileMath.nextMotion(new Vector3(motion.x, motion.y, motion.z), isInWater(), isNoGravity(),
                getGravityEffect());
        setDeltaMovement(next.x(), next.y(), next.z());
    }

    // ---- Damage helpers ----

    /** 1.12.2 {@code damage += (int) health * (damageAmp * ampLevel)}: the damage amp addon adds a share of the health. */
    protected int damageWithAmp(int damage, Entity target) {
        if (ampLevel > 0 && target instanceof LivingEntity living) {
            return ProjectileMath.damageWithAmp(damage, ampLevel, OMTConfig.get().turrets.get(getTurretType()).damageAmp,
                    living.getHealth());
        }
        return damage;
    }

    protected int baseDamage() {
        return OMTConfig.get().turrets.get(getTurretType()).baseDamage;
    }

    /** Damage via {@link TurretDamage#hurt}, with this projectile as direct source. */
    protected void hurt(Entity target, ResourceKey<DamageType> type, float amount) {
        TurretDamage.hurt(level(), target, type, amount, this, basePos, lootingLevel);
        if (amount > 0) {
            notifyHit(target);
        }
    }

    /**
     * Calls the weapon's {@link ProjectileWeapon#onHit hit handler} for an entity this projectile hit, once per entity
     * ({@link #hurt} does this; projectiles that deal no damage call it themselves).
     */
    public void notifyHit(Entity target) {
        if (hitHandler == null || !(level() instanceof ServerLevel serverLevel)) return;
        if (notifiedHits == null) notifiedHits = new HashSet<>();
        if (!notifiedHits.add(target.getId())) return;
        TurretBaseBlockEntity base = basePos != null && serverLevel.isLoaded(basePos)
                && serverLevel.getBlockEntity(basePos) instanceof TurretBaseBlockEntity b ? b : null;
        hitHandler.onHit(serverLevel, this, base, target);
    }

    /**
     * A vanilla explosion at this projectile (block damage, particles, sound) that deals turret damage, so turret
     * rules apply to it (no knockback, loot, and a wand holder is spared, see {@code LivingEntityMixin}).
     */
    protected void explodeHere(float radius, Level.ExplosionInteraction interaction) {
        level().explode(null, TurretDamageSource.of(level(), DamageTypes.EXPLOSION, this, null, basePos, lootingLevel),
                null, getX(), getY(), getZ(), radius, false, interaction);
    }

    /** {@link TurretDamage#canDamage} for this projectile's base. */
    protected boolean canDamage(Entity entity) {
        return TurretDamage.canDamage(level(), basePos, entity);
    }

    // ---- Entity plumbing ----

    /** 1.12.2 {@code isImmuneToExplosions}; grenades and plasma survive explosions. */
    protected boolean isImmuneToExplosions() {
        return false;
    }

    //? if >=1.21 {
    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return isImmuneToExplosions() || super.ignoreExplosion(explosion);
    }
    //?} else {
    /*@Override
    public boolean ignoreExplosion() {
        return isImmuneToExplosions() || super.ignoreExplosion();
    }
    *///?}

    /** 1.12.2 {@code writeToNBTOptional}: projectiles are never saved. */
    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    //? if >=1.21 {
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }
    //?} else {
    /*@Override
    protected void defineSynchedData() {
    }
    *///?}
}

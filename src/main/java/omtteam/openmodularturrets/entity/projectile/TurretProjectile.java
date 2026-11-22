package omtteam.openmodularturrets.entity.projectile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.TurretSetting;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class TurretProjectile extends EntityThrowable implements IEntityAdditionalSpawnData {
    public float gravity;
    public boolean isAmped;
    public int amp_level;
    public int fakeDrops;
    protected TurretBase turretBase;
    ItemStack ammo;
    private BlockPos turretBasePos;
    private int framesRendered = 0;
    /** Strength of the explosion this projectile is setting off ({@link #explodeHere}), 0 at other times. */
    private float explosionStrength;
    /** From the weapon that fired the projectile (see {@link ProjectileWeapon#onHit}). */
    @Nullable
    private ProjectileWeapon.HitHandler hitHandler;
    /** Ids of the entities {@link #hitHandler} was called for. */
    @Nullable
    private Set<Integer> notifiedHits;
    /** Launch point and range (with upgrades) for the range falloff; without them the falloff is off. */
    @Nullable
    private Vec3d launchPos;
    private int launchRange;

    TurretProjectile(World world) {
        super(world);
    }

    TurretProjectile(World world, TurretBase turretBase) {
        super(world);
        this.turretBase = turretBase;
        this.turretBasePos = turretBase.getPos();
        if (turretBase.getLogic().getDamageAmpLevel() > 0) {
            isAmped = true;
            amp_level = turretBase.getLogic().getDamageAmpLevel();
        }
        fakeDrops = turretBase.getLogic().getLootingLevel(3);
    }

    TurretProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world);
        this.ammo = ammo;
        this.turretBase = turretBase;
        this.turretBasePos = turretBase.getPos();
        if (turretBase.getLogic().getDamageAmpLevel() > 0) {
            isAmped = true;
            amp_level = turretBase.getLogic().getDamageAmpLevel();
        }
        fakeDrops = turretBase.getLogic().getLootingLevel(3);
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        buf.writeInt(turretBasePos.getX());
        buf.writeInt(turretBasePos.getY());
        buf.writeInt(turretBasePos.getZ());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        int x, y, z;
        x = additionalData.readInt();
        y = additionalData.readInt();
        z = additionalData.readInt();
        turretBasePos = new BlockPos(x, y, z);
        turretBase = (TurretBase) this.getEntityWorld().getTileEntity(turretBasePos);
    }

    protected boolean canDamagePlayer(EntityPlayer entityPlayer) {
        return TurretDamage.canDamagePlayer(entityPlayer, turretBase);
    }

    protected boolean canDamageEntity(Entity entity) {
        return TurretDamage.canDamageEntity(entity, turretBase);
    }

    public abstract void onHitBlock(IBlockState block, BlockPos pos);

    public abstract void onHitEntity(Entity entity);

    public abstract void playSound();

    public abstract double getDamageAmpBonus();

    /** The turret type whose config (damage, range falloff) applies. */
    protected abstract TurretType getTurretType();

    /**
     * Set by the turret that fires the projectile: launch point and range (with upgrades) for the range falloff
     * (issue 539, {@link #damageFactor(Vec3d)}). A projectile that never gets them deals full damage.
     */
    public void setLaunch(Vec3d origin, int range) {
        this.launchPos = origin;
        this.launchRange = range;
    }

    // ---- Damage helpers ----

    /**
     * {@link ProjectileMath#damageWithAmp}: the damage amp addon adds a share of a living target's health.
     *
     * @deprecated whole points and no range falloff; use {@link #damage(Entity)} or {@link #damage(float, Entity)}.
     */
    @Deprecated
    protected int damageWithAmp(int damage, Entity target) {
        if (isAmped && target instanceof EntityLivingBase) {
            return ProjectileMath.damageWithAmp(damage, amp_level, getDamageAmpBonus(),
                    ((EntityLivingBase) target).getHealth());
        }
        return damage;
    }

    /**
     * The turret type's configured damage per hit, before the damage amp addon and the range falloff.
     * For a hit, prefer {@link #damage(Entity)}, which applies both.
     */
    protected int baseDamage() {
        return ConfigLogicSettings.setting(getTurretType()).baseDamage;
    }

    /**
     * Share of the damage a hit at {@code point} deals (range falloff, {@link ProjectileMath#falloff}): measured from
     * the launch point ({@link #setLaunch}) with the turret type's {@code optimalRange} and {@code falloffDamage}.
     * 1 if the projectile has no launch point.
     */
    protected float damageFactor(Vec3d point) {
        if (launchPos == null) return 1.0F;
        TurretSetting setting = ConfigLogicSettings.setting(getTurretType());
        return (float) ProjectileMath.falloff(launchPos.distanceTo(point), launchRange, setting.optimalRange,
                setting.falloffDamage);
    }

    /** {@link #damageFactor(Vec3d)} at the projectile's position: for explosions and bursts (the same for every victim). */
    protected float damageFactor() {
        return damageFactor(getPositionVector());
    }

    /**
     * Where the projectile is when this tick's sweep ({@link #onUpdate()}: its bounding box moved along 1.2 times its
     * motion) first touches the target, or the projectile's position if the sweep does not touch it (or touches it
     * from the start). The target's box is grown by the projectile's own box, so the projectile's position path
     * enters it exactly where the projectile's box would touch the target.
     */
    protected Vec3d hitPoint(Entity target) {
        Vec3d position = getPositionVector();
        double halfWidth = width / 2.0D;
        AxisAlignedBB reach = target.getEntityBoundingBox().expand(0, -height, 0).grow(halfWidth, 0, halfWidth);
        // Inside the box from the start: 1.12's calculateIntercept would return the exit face (1.21.11's clip gives none).
        if (reach.contains(position)) return position;
        RayTraceResult clip = reach.calculateIntercept(position,
                position.add(motionX * 1.2D, motionY * 1.2D, motionZ * 1.2D));
        return clip != null ? clip.hitVec : position;
    }

    /** {@code damage} plus the damage amp addon's bonus for {@code target} (no rounding, no range falloff). */
    protected float ampDamage(float damage, Entity target) {
        if (isAmped && target instanceof EntityLivingBase) {
            return ProjectileMath.damageWithAmp(damage, amp_level, getDamageAmpBonus(),
                    ((EntityLivingBase) target).getHealth());
        }
        return damage;
    }

    /** Damage of a direct hit on {@code target}: {@code base} with the damage amp bonus and the range falloff at the hit point. */
    protected float damage(float base, Entity target) {
        return ampDamage(base, target) * damageFactor(hitPoint(target));
    }

    /** {@link #damage(float, Entity)} with {@link #baseDamage()}. */
    protected float damage(Entity target) {
        return damage(baseDamage(), target);
    }

    @Override
    public void onUpdate() {
        if (this.ticksExisted > ProjectileMath.MAX_AGE) {
            this.setDead();
            return;
        }

        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;

        Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
        Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        RayTraceResult raytraceresult = this.getEntityWorld().rayTraceBlocks(vec3d, vec3d1);
        List<Entity> list = this.getEntityWorld().getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(motionX * 1.2D, motionY * 1.2D, motionZ * 1.2D));

        for (Entity entity : list) {
            if (entity.canBeCollidedWith()) {
                this.onHitEntity(entity);
            }
        }

        if (raytraceresult != null) {
            if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK) {
                this.onHitBlock(this.getEntityWorld().getBlockState(raytraceresult.getBlockPos()), raytraceresult.getBlockPos());
            }
        }

        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
        boolean inWater = this.isInWater();
        if (inWater) {
            for (int j = 0; j < 4; ++j) {
                this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
            }
        }

        // drag (stronger in water) and gravity, as the core predicts the flight for aiming
        Vector3 next = ProjectileMath.nextMotion(new Vector3(this.motionX, this.motionY, this.motionZ), inWater,
                this.hasNoGravity(), this.getGravityVelocity());
        this.motionX = next.x();
        this.motionY = next.y();
        this.motionZ = next.z();

        this.setPosition(this.posX, this.posY, this.posZ);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean writeToNBTOptional(NBTTagCompound nbtTagCompound) {
        this.setDead();
        return false;
    }

    /** Set by the turret that fires the projectile, from its {@link ProjectileWeapon}. */
    public void setHitHandler(@Nullable ProjectileWeapon.HitHandler hitHandler) {
        this.hitHandler = hitHandler;
    }

    /**
     * Calls the weapon's {@link ProjectileWeapon#onHit hit handler} for an entity this projectile hit, once per entity
     * (the projectiles do this after damaging it; projectiles that deal no damage call it themselves).
     */
    public void notifyHit(Entity target) {
        if (hitHandler == null || !(getEntityWorld() instanceof WorldServer)) return;
        if (notifiedHits == null) notifiedHits = new HashSet<>();
        if (!notifiedHits.add(target.getEntityId())) return;
        WorldServer world = (WorldServer) getEntityWorld();
        TurretBase base = null;
        if (turretBasePos != null && world.isBlockLoaded(turretBasePos)
                && world.getTileEntity(turretBasePos) instanceof TurretBase) {
            base = (TurretBase) world.getTileEntity(turretBasePos);
        }
        hitHandler.onHit(world, this, base, target);
    }

    protected void setTagsForTurretHit(Entity entity) {
        EntityLivingBase entityLivingBase;
        if (entity instanceof EntityLivingBase) {
            entityLivingBase = (EntityLivingBase) entity;
            if (!(entityLivingBase instanceof EntityPlayer) && !entityLivingBase.getTags().contains("openmodularturrets:turret_hit")) {
                entityLivingBase.addTag("openmodularturrets:turret_hit");
            }
            if (!(entityLivingBase instanceof EntityPlayer) && this.fakeDrops > -1) {
                entityLivingBase.addTag("openmodularturrets:fake_drops_" + this.fakeDrops);
            }
        }
    }

    /**
     * A vanilla explosion at this projectile (particles, sound, and block damage only with {@code destroyBlocks})
     * whose entity damage is turret damage (1.20.1+ {@code explodeHere}): {@code OMTEventHandler} hands the entities
     * it affects to {@link #affectByExplosion}.
     */
    protected void explodeHere(float strength, boolean destroyBlocks) {
        explosionStrength = strength;
        try {
            getEntityWorld().createExplosion(this, posX, posY, posZ, strength, destroyBlocks);
        } finally {
            explosionStrength = 0;
        }
    }

    /**
     * Called from {@code ExplosionEvent.Detonate} for an explosion of this projectile: deals vanilla's explosion
     * damage and push ({@code Explosion.doExplosionA}) to the affected entities, but with a turret damage source
     * (kill counting, loot rules, no knockback), and spares {@linkplain TurretImmunity immune} entities. Empties
     * {@code entities}, so vanilla does not hurt them again.
     *
     * @return false if this projectile is not setting off an explosion (the entities are left to vanilla)
     */
    public boolean affectByExplosion(Explosion explosion, List<Entity> entities) {
        if (explosionStrength <= 0 || !(getEntityWorld() instanceof WorldServer)) return false;
        WorldServer world = (WorldServer) getEntityWorld();
        double diameter = explosionStrength * 2.0F;
        Vec3d centre = explosion.getPosition();
        for (Entity entity : entities) {
            if (entity.isImmuneToExplosions() || TurretImmunity.isImmune(entity)) continue;
            double distance = entity.getDistance(centre.x, centre.y, centre.z) / diameter;
            if (distance > 1.0D) continue;
            double dx = entity.posX - centre.x;
            double dy = entity.posY + entity.getEyeHeight() - centre.y;
            double dz = entity.posZ - centre.z;
            double length = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            if (length == 0.0D) continue;
            dx /= length;
            dy /= length;
            dz /= length;
            double impact = (1.0D - distance) * world.getBlockDensity(centre, entity.getEntityBoundingBox());
            // vanilla's explosion damage and source flags (DamageSource.causeExplosionDamage)
            DamageSource source = new NormalDamageSource("explosion", fakeDrops, turretBase, world, false, entity)
                    .setDifficultyScaled().setExplosion();
            if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
            entity.attackEntityFrom(source, (float) ((int) ((impact * impact + impact) / 2.0D * 7.0D * diameter + 1.0D)));
            double push = entity instanceof EntityLivingBase
                    ? EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, impact) : impact;
            entity.motionX += dx * push;
            entity.motionY += dy * push;
            entity.motionZ += dz * push;
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.isSpectator() && (!player.isCreative() || !player.capabilities.isFlying)) {
                    explosion.getPlayerKnockbackMap().put(player, new Vec3d(dx * impact, dy * impact, dz * impact));
                }
            }
        }
        entities.clear();
        return true;
    }

    public int getFramesRendered() {
        return framesRendered;
    }

    public void setFramesRendered(int framesRendered) {
        this.framesRendered = framesRendered;
    }
}

package omtteam.openmodularturrets.tileentity.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.NetworkUtil;
import omtteam.omlib.util.TargetingSettings;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.api.tileentity.ITurretHead;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.network.MessageUpdateTurret;
import omtteam.openmodularturrets.network.OMTNetworkingHandler;
import omtteam.openmodularturrets.registry.ModSounds;
import omtteam.openmodularturrets.registry.TurretRegistry;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.base.TurretBaseWorld;
import omtteam.openmodularturrets.turret.AreaWeapon;
import omtteam.openmodularturrets.turret.EffectWeapon;
import omtteam.openmodularturrets.turret.ProjectileWeapon;
import omtteam.openmodularturrets.turret.RayWeapon;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretWeapon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A turret (1.12.2 {@code TurretHead} / {@code AbstractDirectedTurret} / {@code ProjectileTurret} /
 * {@code RayTracingTurret} and the 14 turret tile entities; 1.20.1+ {@code TurretBlockEntity}). Thin by design: the
 * base owns targeting, ammo, energy and upgrades; the turret aims at the base's target, keeps its cooldown
 * ({@link TurretState}) and fires. The weapon ({@link TurretWeapon}) comes from the turret's definition.
 */
public class TurretTileEntity extends TileEntityBase implements ITurretHead, ITickable, ITurretBaseAddonTileEntity {
    /** 1.12.2 {@code AbstractDirectedTurret.update}: the aim was sent to clients every 5 ticks. */
    private static final int SYNC_INTERVAL = 5;
    /** Smaller aim changes are not synced. */
    private static final float SYNC_THRESHOLD_DEGREES = 0.5F;
    /** Share of the remaining angle the client model turns per tick. */
    private static final float CLIENT_TURN_RATE = 0.5F;
    /** Degrees the melee turret's top piece turns per tick while active. */
    private static final float CLIENT_SPIN_RATE = 36.0F;
    /** 1.12.2 {@code RayTracingTurret.forceShot}: forced rays end 5 blocks in front of the turret. */
    private static final double FORCED_RAY_LENGTH = 5D;

    /** Cooldown and concealment. */
    private final TurretState state = new TurretState();

    /** Direction from the base to this turret; null until the base was found. */
    @Nullable
    private EnumFacing facing;
    /** Aim relative to the mounting face (see {@link TurretMath}), in degrees; synced to clients. */
    private float yaw;
    private float pitch;
    private float syncedYaw;
    private float syncedPitch;
    private int ticksSinceSync;
    /** Whether the base is active and the turret has a target (melee turret: something in reach); synced. */
    private boolean active;
    private boolean activeChanged;
    /** 3.x API (ComputerCraft / OpenComputers): fire along the current aim while there is no target. */
    private boolean autoFire;

    /** Ammo taken by the last {@link #beginShot}, if the turret uses ammo. */
    @Nullable
    private AmmoDraw lastAmmo;

    /** Target tracking for the lead (positions per tick; players have no motion on the server). */
    private int trackedTargetId = -1;
    @Nullable
    private Vec3d trackedTargetPos;

    /** Client only: smoothed render angles. */
    private float renderYaw;
    private float renderPitch;
    private float prevRenderYaw;
    private float prevRenderPitch;
    private float renderSpin;
    private float prevRenderSpin;
    private boolean renderInitialised;

    public TurretTileEntity() {
    }

    // ---- What this turret is ----

    /** The core turret type of the turret block; null if the block is not a turret (any more). */
    @Nullable
    public TurretType getCoreType() {
        return world != null && world.getBlockState(pos).getBlock() instanceof BlockTurret
                ? ((BlockTurret) world.getBlockState(pos).getBlock()).getCoreType() : null;
    }

    /** The 3.x turret type (block id and config section) of the turret block. */
    public omtteam.openmodularturrets.turret.TurretType getTurretType() {
        return ((BlockTurret) world.getBlockState(pos).getBlock()).getTurretType();
    }

    /** What a shot of this turret does. */
    public TurretWeapon getWeapon() {
        return TurretRegistry.get(getCoreType()).weapon();
    }

    /** Direction from the base to this turret, looked up from the neighbouring base; null without base. */
    @Nullable
    public EnumFacing getFacing() {
        if (facing == null && world != null) {
            EnumFacing towardsBase = TurretHeadUtil.getTurretBaseFacing(world, pos);
            facing = towardsBase == null ? null : towardsBase.getOpposite();
        }
        return facing;
    }

    /** The core face of {@link #getFacing()}. */
    @Nullable
    public Face getFace() {
        EnumFacing current = getFacing();
        return current == null ? null : TurretBase.toFace(current);
    }

    /** The base this turret is mounted on; null without base. */
    @Override
    @Nullable
    public TurretBase getBase() {
        EnumFacing current = getFacing();
        if (world == null || current == null) return null;
        BlockPos basePos = pos.offset(current.getOpposite());
        return world.isBlockLoaded(basePos) && world.getTileEntity(basePos) instanceof TurretBase
                ? (TurretBase) world.getTileEntity(basePos) : null;
    }

    @Nonnull
    @Override
    @SuppressWarnings("ConstantConditions")
    public TileEntityOwnedBlock getLinkedBlock() {
        return getBase();
    }

    /** Whether the base conceals this turret right now (the block state of the turret). */
    public boolean isConcealed() {
        return world != null && world.getBlockState(pos).getBlock() instanceof BlockTurret
                && world.getBlockState(pos).getValue(BlockTurret.CONCEALED);
    }

    // ---- Server ----

    @Override
    public void update() {
        if (world.isRemote) {
            clientTick();
            return;
        }
        EnumFacing current = getFacing();
        if (current != null && !world.isBlockLoaded(pos.offset(current.getOpposite()))) return;
        TurretBase base = getBase();
        TurretType type = getCoreType();
        if (type == null) return;
        // a turret without base, on a base of a lower tier or on a face it cannot mount on drops itself
        if (base == null || base.isInvalid() || !base.getLogic().supportsTurret(type) || !type.canMountOn(getFace())) {
            facing = null;
            world.destroyBlock(pos, true);
            return;
        }
        TurretBaseLogic logic = base.getLogic();
        // a base whose turret was not loaded when it scanned its neighbours learns about it now
        if (logic.getTurrets().get(getFace()) != type) {
            base.updateNeighbours();
        }
        state.tick();
        ticksSinceSync++;
        TurretWeapon weapon = getWeapon();
        boolean engaging = false;
        if (logic.isActive() && weapon instanceof AreaWeapon) {
            engaging = strikeInReach((WorldServer) world, base, (AreaWeapon) weapon);
        } else if (logic.isActive()) {
            EntityLivingBase target = base.getTargetEntity(current);
            updateConcealment(logic, target != null);
            engaging = target != null;
            if (target != null) {
                engage(target);
            } else {
                forgetTarget();
                if (autoFire && state.isReady(logic, type)) {
                    forceShot();
                }
            }
        } else {
            forgetTarget();
        }
        if (engaging != active) {
            active = engaging;
            activeChanged = true;
        }
        syncAimIfChanged();
    }

    /**
     * Whether the base is active and this turret has a target (melee turret: something in reach); synced to
     * clients (the melee turret spins).
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 1.12.2 {@code AbstractDirectedTurret.update} / {@code TurretHead.updateChecks}: aiming turrets turn towards the
     * target's eyes every tick (within {@link TurretMath#MIN_PITCH}, the base only picks targets above it), then the
     * weapon fires once the cooldown passed. Projectiles are shot at the predicted position.
     */
    private void engage(EntityLivingBase target) {
        TurretWeapon weapon = getWeapon();
        Vec3d centre = centre();
        if (weapon.aims()) {
            aimAt(target);
        }

        Vec3d position = target.getPositionVector();
        Vector3 velocity = trackedTargetId == target.getEntityId() && trackedTargetPos != null
                ? new Vector3(position.x - trackedTargetPos.x, position.y - trackedTargetPos.y,
                position.z - trackedTargetPos.z)
                : Vector3.ZERO;
        trackedTargetId = target.getEntityId();
        trackedTargetPos = position;

        if (weapon instanceof ProjectileWeapon) {
            Vector3 toBody = new Vector3(target.posX - centre.x, aimHeight(target, centre) - centre.y,
                    target.posZ - centre.z);
            fire(toBody, velocity, target);
        } else if (weapon instanceof RayWeapon) {
            TurretBase base = beginShot();
            if (base != null) {
                Vec3d end = new Vec3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
                RayWeapons.shoot(this, base, (RayWeapon) weapon, end, target.height, rayOrigin(), rayTip());
            }
        } else if (weapon instanceof EffectWeapon) {
            TurretBase base = beginShot();
            if (base != null) {
                EffectWeapons.fireEffect(this, base, (EffectWeapon) weapon, target);
            }
        }
    }

    /** Turns towards the target's eyes, within {@link TurretMath#limit}. */
    private void aimAt(EntityLivingBase target) {
        Vec3d centre = centre();
        Vector3 toEyes = new Vector3(target.posX - centre.x, target.posY + target.getEyeHeight() - centre.y,
                target.posZ - centre.z);
        TurretMath.Angles angles = TurretMath.limit(TurretMath.anglesFor(getFace(), toEyes));
        yaw = angles.yaw();
        pitch = angles.pitch();
    }

    /**
     * Height to shoot at: the body centre, or the eyes when a ledge or the like hides the centre (the base only
     * picks targets whose eyes are visible, so the shot would otherwise hit the block in front of the target).
     */
    private double aimHeight(EntityLivingBase target, Vec3d centre) {
        double body = target.posY + target.height * 0.5D;
        double eyes = target.posY + target.getEyeHeight();
        if (eyes <= body || TurretBaseWorld.isClear(world, centre, new Vec3d(target.posX, body, target.posZ))) {
            return body;
        }
        return eyes;
    }

    /**
     * Area weapons (melee turret) ignore the base's target: the turret is active while anything it may damage is in
     * the weapon's area and strikes it once the cooldown passed.
     *
     * @return whether anything is in reach
     */
    private boolean strikeInReach(WorldServer world, TurretBase base, AreaWeapon weapon) {
        List<EntityLivingBase> victims = new ArrayList<>();
        for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, weapon.area().apply(this))) {
            if (entity.isEntityAlive() && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).isSpectator())
                    && omtteam.openmodularturrets.util.OMTUtil.canDamageEntity(entity, base)) {
                victims.add(entity);
            }
        }
        updateConcealment(base.getLogic(), !victims.isEmpty());
        if (victims.isEmpty()) return false;
        TurretBase shooter = beginShot();
        if (shooter != null) {
            EffectWeapons.fireArea(this, shooter, weapon, victims);
        }
        return true;
    }

    private void forgetTarget() {
        trackedTargetId = -1;
        trackedTargetPos = null;
    }

    /** Sends the aim every {@link #SYNC_INTERVAL} ticks if it changed, a change of {@link #active} at once. */
    private void syncAimIfChanged() {
        boolean aimChanged = ticksSinceSync >= SYNC_INTERVAL
                && (Math.abs(TurretMath.angleDifference(syncedYaw, yaw)) > SYNC_THRESHOLD_DEGREES
                || Math.abs(pitch - syncedPitch) > SYNC_THRESHOLD_DEGREES);
        if (aimChanged || activeChanged) {
            syncedYaw = yaw;
            syncedPitch = pitch;
            ticksSinceSync = 0;
            activeChanged = false;
            OMTNetworkingHandler.INSTANCE.sendToAllTracking(new MessageUpdateTurret(this),
                    NetworkUtil.getTargetPointFromBlockPos(world.provider.getDimension(), pos, 100));
        }
    }

    /** Applies {@link TurretState#updateConcealment}: a retracted turret is not drawn and has no shape. */
    private void updateConcealment(TurretBaseLogic logic, boolean hasTarget) {
        switch (state.updateConcealment(logic.isConcealingTurrets(), isConcealed(), hasTarget)) {
            case DEPLOY:
                playSound(ModSounds.turretDeploySound);
                setConcealed(false);
                break;
            case RETRACT:
                playSound(ModSounds.turretRetractSound);
                setConcealed(true);
                break;
            case REVEAL:
                setConcealed(false);
                break;
            default:
                break;
        }
    }

    private void setConcealed(boolean concealed) {
        if (isConcealed() != concealed) {
            world.setBlockState(pos, world.getBlockState(pos).withProperty(BlockTurret.CONCEALED, concealed), 3);
        }
    }

    /**
     * Starts a shot ({@link TurretState#beginShot}) and keeps its ammo in {@link #lastAmmo}.
     *
     * @return the base, or null if the turret cannot shoot now (nothing was consumed)
     */
    @Nullable
    private TurretBase beginShot() {
        lastAmmo = null;
        TurretBase base = getBase();
        TurretType type = getCoreType();
        if (base == null || type == null) return null;
        Optional<TurretState.Shot> shot = state.beginShot(base.getLogic(), type);
        if (!shot.isPresent()) return null;
        lastAmmo = shot.get().ammo().orElse(null);
        return base;
    }

    /** The drawn ammo as the fired item with the count drawn, empty if none was drawn. */
    private ItemStack lastAmmoStack() {
        if (lastAmmo == null || !(lastAmmo.item() instanceof ItemStack)) return ItemStack.EMPTY;
        ItemStack stack = ((ItemStack) lastAmmo.item()).copy();
        stack.setCount(lastAmmo.count());
        return stack;
    }

    /**
     * Fires projectiles (1.12.2 {@code ProjectileTurret.shootProjectile}): one per scattershot level, with the
     * turret's inaccuracy ({@link ProjectileMath#inaccuracy}), led by {@link TurretMath#lead}.
     *
     * @param toAim    aim point minus turret centre
     * @param velocity target movement per tick
     * @return whether a shot was fired
     */
    private boolean fire(Vector3 toAim, Vector3 velocity, @Nullable EntityLivingBase target) {
        if (!(getWeapon() instanceof ProjectileWeapon)) return false;
        ProjectileWeapon weapon = (ProjectileWeapon) getWeapon();
        TurretBase base = beginShot();
        if (base == null) return false;
        TurretBaseLogic logic = base.getLogic();
        TurretType type = getCoreType();
        float inaccuracy = ProjectileMath.inaccuracy(logic.getAccuracyDeviation(type));
        Vec3d origin = centre();
        ItemStack ammo = lastAmmoStack();
        for (int i = 0; i <= logic.getScattershotCount(type); i++) {
            TurretProjectile projectile = weapon.factory().create(world, ammo, target, base);
            if (projectile == null) continue;
            projectile.setPosition(origin.x, origin.y, origin.z);
            // the lead depends on the projectile's gravity, so it is worked out per projectile
            TurretMath.Lead lead = TurretMath.lead(toAim, velocity, weapon.speed(),
                    projectile.hasNoGravity() ? 0 : projectile.gravity);
            Vector3 direction = lead.direction();
            projectile.shoot(direction.x(), direction.y(), direction.z(), weapon.speed() * lead.speedFactor(),
                    inaccuracy);
            if (logic.getDamageAmpLevel() > 0) {
                playSound(ModSounds.amped);
            }
            playSound(weapon.launchSound());
            world.spawnEntity(projectile);
        }
        return true;
    }

    /**
     * 3.x API ({@code ITurretBase.forceShootTurret}, auto fire): shoots along the current aim without a target. Only
     * aiming weapons can do that.
     *
     * @return whether a shot was fired
     */
    public boolean forceShot() {
        if (world == null || world.isRemote || getFace() == null) return false;
        TurretWeapon weapon = getWeapon();
        Vector3 direction = TurretMath.direction(getFace(), new TurretMath.Angles(yaw, pitch));
        if (weapon instanceof ProjectileWeapon) {
            return fire(direction.scale(FORCED_RAY_LENGTH), Vector3.ZERO, null);
        }
        if (weapon instanceof RayWeapon) {
            TurretBase base = beginShot();
            if (base == null) return false;
            Vec3d origin = rayOrigin();
            Vec3d end = origin.addVector(direction.x() * FORCED_RAY_LENGTH, direction.y() * FORCED_RAY_LENGTH,
                    direction.z() * FORCED_RAY_LENGTH);
            RayWeapons.shoot(this, base, (RayWeapon) weapon, end, 1.0F, origin, rayTip());
            return true;
        }
        return false;
    }

    private Vec3d centre() {
        return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    /** Rays start 0.1 blocks above the turret centre (1.12.2 for turrets on top of a base). */
    private Vec3d rayOrigin() {
        Vector3 up = Vector3.of(getFace());
        return centre().addVector(up.x() * 0.1D, up.y() * 0.1D, up.z() * 0.1D);
    }

    /** The tip of the turret, where the arc is drawn from. */
    private Vec3d rayTip() {
        Vector3 up = Vector3.of(getFace());
        return centre().addVector(up.x() * 0.5D, up.y() * 0.5D, up.z() * 0.5D);
    }

    void playSound(SoundEvent sound) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, (float) OMTConfig.TURRETS.turretSoundVolume,
                world.rand.nextFloat() + 0.5F);
    }

    // ---- Client ----

    /** Turns the rendered model smoothly towards the synced aim and spins it while active. */
    private void clientTick() {
        prevRenderYaw = renderYaw;
        prevRenderPitch = renderPitch;
        renderYaw += TurretMath.angleDifference(renderYaw, yaw) * CLIENT_TURN_RATE;
        renderPitch += (pitch - renderPitch) * CLIENT_TURN_RATE;
        // kept in [0, 360) so the interpolation never jumps
        renderSpin %= 360.0F;
        prevRenderSpin = renderSpin;
        if (active) {
            renderSpin += CLIENT_SPIN_RATE;
        }
    }

    /** Interpolated spin angle of the melee turret's top piece, in degrees. */
    public float getRenderSpin(float partialTick) {
        return prevRenderSpin + (renderSpin - prevRenderSpin) * partialTick;
    }

    /** Client sync of {@link #isActive()}. */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** Interpolated yaw for rendering, relative to the mounting face. */
    public float getRenderYaw(float partialTick) {
        return prevRenderYaw + TurretMath.angleDifference(prevRenderYaw, renderYaw) * partialTick;
    }

    /** Interpolated pitch for rendering, relative to the mounting face. */
    public float getRenderPitch(float partialTick) {
        return prevRenderPitch + (renderPitch - prevRenderPitch) * partialTick;
    }

    // ---- Aim (synced, 3.x API) ----

    /** Yaw relative to the mounting face, in degrees (see {@link TurretMath}). */
    public float getYaw() {
        return yaw;
    }

    /** Pitch above the mounting plane, in degrees (see {@link TurretMath}). */
    public float getPitch() {
        return pitch;
    }

    /** Sets the aim (3.x API and client sync); the pitch is limited to {@link TurretMath#MIN_PITCH}. */
    public void setYawPitch(float yaw, float pitch) {
        TurretMath.Angles angles = TurretMath.limit(new TurretMath.Angles(yaw, pitch));
        this.yaw = angles.yaw();
        this.pitch = angles.pitch();
        if (world != null && world.isRemote && !renderInitialised) {
            renderYaw = prevRenderYaw = this.yaw;
            renderPitch = prevRenderPitch = this.pitch;
            renderInitialised = true;
        }
    }

    // ---- ITurretHead (3.x API) ----

    @Override
    public TargetingSettings getTargetingSettings() {
        return getBase().getTargetingSettings();
    }

    /** The targeting weights of the turret type (3.x saved them per turret, but they were never changed). */
    @Override
    public Integer[] getPriorities() {
        int[] weights = getCoreType().getDefaultPriorities();
        Integer[] result = new Integer[weights.length];
        for (int i = 0; i < weights.length; i++) {
            result[i] = weights[i];
        }
        return result;
    }

    @Override
    public boolean isOnCooldown() {
        TurretBase base = getBase();
        return base == null || state.getTicksSinceShot() < base.getLogic().getFireCooldownTicks(getCoreType());
    }

    @Override
    public boolean getAutoFire() {
        return autoFire;
    }

    @Override
    public void setAutoFire(boolean autoFire) {
        this.autoFire = autoFire;
    }

    @Override
    public int getTurretBasePowerUsage() {
        return ConfigLogicSettings.INSTANCE.turret(getCoreType()).powerUsage();
    }

    @Override
    public int getTurretBaseFireRate() {
        return ConfigLogicSettings.INSTANCE.turret(getCoreType()).baseFireRate();
    }

    @Override
    public double getBaseTurretAccuracyDeviation() {
        return ConfigLogicSettings.INSTANCE.turret(getCoreType()).baseAccuracyDeviation();
    }

    @Override
    public int getTurretBaseRange() {
        return ConfigLogicSettings.INSTANCE.turret(getCoreType()).baseRange();
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return ConfigLogicSettings.INSTANCE.turret(getCoreType()).damageAmp();
    }

    @Override
    public double getActualTurretAccuracyDeviation() {
        return getBase().getLogic().getAccuracyDeviation(getCoreType());
    }

    /** Nothing is cached; the stats are read from the base logic on use. */
    @Override
    public void triggerResetCaches() {
    }

    /** Ticks between shots, with the base's fire rate upgrades. */
    public double getFireCooldownTicks() {
        return getBase().getLogic().getFireCooldownTicks(getCoreType());
    }

    /** Range with the base's range upgrades. */
    public int getTurretRange() {
        return getBase().getLogic().getRange(getCoreType());
    }

    /** Shots left in the base: matching ammo items, or the energy for energy turrets. */
    public int getAmmoLevel() {
        return getBase().getLogic().getAmmoLevel(getCoreType());
    }

    /** Damage amp addons of the base. */
    public int getDamageAmpLevel() {
        return getBase().getLogic().getDamageAmpLevel();
    }

    // ---- NBT (3.x keys) ----

    @Override
    public NBTTagCompound saveToNBT(NBTTagCompound tag) {
        super.saveToNBT(tag);
        tag.setInteger("ticksBeforeFire", state.getTicksSinceShot());
        tag.setBoolean("autoFire", autoFire);
        tag.setFloat("yaw", yaw);
        tag.setFloat("pitch", pitch);
        tag.setBoolean("active", active);
        return tag;
    }

    /** Reads the 3.x keys; 3.x yaw and pitch were world angles, they are replaced at the first aim. */
    @Override
    public void loadFromNBT(NBTTagCompound tag) {
        super.loadFromNBT(tag);
        state.setTicksSinceShot(tag.getInteger("ticksBeforeFire"));
        autoFire = tag.getBoolean("autoFire");
        setYawPitch(tag.getFloat("yaw"), tag.getFloat("pitch"));
        active = tag.getBoolean("active");
    }
}

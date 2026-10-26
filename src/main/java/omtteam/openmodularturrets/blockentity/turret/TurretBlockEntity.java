package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.api.weapon.AreaWeapon;
import omtteam.openmodularturrets.api.weapon.EffectWeapon;
import omtteam.openmodularturrets.api.weapon.ProjectileWeapon;
import omtteam.openmodularturrets.api.weapon.RayWeapon;
import omtteam.openmodularturrets.api.weapon.SelfTargetingWeapon;
import omtteam.openmodularturrets.api.weapon.TurretWeapon;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.BlockEntityBaseWorld;
import omtteam.openmodularturrets.blockentity.base.LivingTargetEntity;
import omtteam.omlib.compat.CompatBlockEntity;
import omtteam.openmodularturrets.compat.SubLevelCompat;
import omtteam.omlib.compat.nbt.NbtCompat;
import omtteam.openmodularturrets.config.ConfigLogicSettings;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.base.TurretPlacement;
import omtteam.openmodularturrets.core.turret.ProjectileMath;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretState;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Pose;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.entity.projectile.TurretDamage;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModSounds;
import omtteam.openmodularturrets.registry.TurretRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A turret (1.12.2 {@code TurretHead} / {@code AbstractDirectedTurret} / {@code ProjectileTurret}). Thin by design: the
 * base owns targeting, ammo, energy and upgrades; the turret aims at the base's target, keeps its cooldown and
 * fires. The weapon ({@link TurretWeapon}: projectile, ray, effect, area or self-targeting) comes from the turret's definition.
 */
public class TurretBlockEntity extends CompatBlockEntity {
    /** 1.12.2 {@code AbstractDirectedTurret.update}: the aim was sent to clients every 5 ticks. */
    private static final int SYNC_INTERVAL = 5;
    /** Smaller aim changes are not synced. */
    private static final float SYNC_THRESHOLD_DEGREES = 0.5F;
    /** Share of the remaining angle the client model turns per tick. */
    private static final float CLIENT_TURN_RATE = 0.5F;
    /** Degrees the melee turret's top piece turns per tick while active. */
    private static final float CLIENT_SPIN_RATE = 36.0F;

    /** Cooldown and concealment (see {@link TurretState}). */
    private final TurretState state = new TurretState();

    /** Aim relative to the mounting face (see {@link TurretMath}), in degrees; synced to clients. */
    private float yaw;
    private float pitch;
    private float syncedYaw;
    private float syncedPitch;
    private int ticksSinceSync;
    private boolean active;

    /** Ammo taken by the last {@link #beginShot()}, if the turret uses ammo. */
    @Nullable
    private AmmoDraw lastAmmo;

    /** Target tracking for the lead (1.12.2 tracked players manually, others via prevPos). */
    private int trackedTargetId = -1;
    @Nullable
    private Vec3 trackedTargetPos;

    /** {@link #getMount()} of the current tick, and the game time it was made at. */
    @Nullable
    private TurretMount mount;
    private long mountTime;

    /** {@link SelfTargetingWeapon}: ticks until the picker is asked again, and whether it last picked anything. */
    private int ticksUntilPick;
    private boolean picked;

    /** Client only: smoothed render angles. */
    private float renderYaw;
    private float renderPitch;
    private float prevRenderYaw;
    private float prevRenderPitch;
    private float renderSpin;
    private float prevRenderSpin;
    private boolean renderInitialised;

    public TurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TURRET, pos, state);
    }

    public TurretType getTurretType() {
        return ((TurretBlock) getBlockState().getBlock()).getTurretType();
    }

    /** What a shot of this turret does (from its {@link omtteam.openmodularturrets.api.TurretDefinition}). */
    public TurretWeapon getWeapon() {
        return TurretRegistry.get(getTurretType()).weapon();
    }

    /** Direction from the base to this turret. */
    public Direction getFacing() {
        return getBlockState().getValue(TurretBlock.FACING);
    }

    public Face getFace() {
        return Face.valueOf(getFacing().name());
    }

    public BlockPos getBasePos() {
        return worldPosition.relative(getFacing().getOpposite());
    }

    public Optional<TurretBaseBlockEntity> getBase() {
        if (level == null || !level.isLoaded(getBasePos())) return Optional.empty();
        return level.getBlockEntity(getBasePos()) instanceof TurretBaseBlockEntity base ? Optional.of(base) : Optional.empty();
    }

    /**
     * Where the turret is in the world this tick (on a Sable contraption: where its plot position is shown). Made once
     * per game tick, so everything in a tick uses the same pose of the contraption.
     */
    public TurretMount getMount() {
        long time = level != null ? level.getGameTime() : 0L;
        if (mount == null || mountTime != time) {
            mount = createMount();
            mountTime = time;
        }
        return mount;
    }

    private TurretMount createMount() {
        Vec3 plotCentre = Vec3.atCenterOf(worldPosition);
        Pose pose = level != null ? SubLevelCompat.poseAt(level, worldPosition) : Pose.IDENTITY;
        Vector3 up = pose.directionToWorld(Vector3.of(getFace()));
        Vec3 velocity = pose.isIdentity() ? Vec3.ZERO : SubLevelCompat.velocityAt(level, plotCentre);
        return new TurretMount(pose, getFace(), TurretMount.toVec3(pose.toWorld(TurretMount.toVector(plotCentre))),
                TurretMount.toVec3(up.scale(1.0D / up.length())), velocity);
    }

    // ---- Server ----

    @SuppressWarnings("unused")
    public static void serverTick(Level level, BlockPos pos, BlockState state, TurretBlockEntity turret) {
        BlockPos basePos = turret.getBasePos();
        if (!level.isLoaded(basePos)) return;
        Optional<TurretBaseBlockEntity> base = turret.getBase();
        TurretType type = turret.getTurretType();
        // a turret without base or one that may not stay there (TurretPlacement.canStay) drops itself
        if (base.isEmpty() || !TurretPlacement.canStay(type, turret.getFace(), base.get().getLogic().getTier(),
                base.get().getLogic().getTurrets(), ConfigLogicSettings.INSTANCE).allowed()) {
            level.destroyBlock(pos, true);
            return;
        }
        turret.state.tick();
        turret.ticksSinceSync++;
        TurretBaseLogic logic = base.get().getLogic();
        boolean engaging = false;
        if (logic.isActive() && turret.getWeapon() instanceof AreaWeapon area) {
            engaging = turret.strikeInReach((ServerLevel) level, base.get(), area);
        } else if (logic.isActive() && turret.getWeapon() instanceof SelfTargetingWeapon weapon) {
            engaging = turret.pickAndFire((ServerLevel) level, base.get(), weapon);
        } else if (logic.isActive()) {
            Optional<Entity> target = logic.getTarget(turret.getFace())
                    .filter(LivingTargetEntity.class::isInstance)
                    .map(t -> ((LivingTargetEntity) t).getEntity());
            turret.updateConcealment(logic, target.isPresent());
            target.ifPresentOrElse(turret::engage, turret::forgetTarget);
            engaging = target.isPresent();
        } else {
            turret.forgetTarget();
        }
        if (engaging != turret.active) {
            turret.active = engaging;
            turret.syncToClient();
        }
        turret.syncAimIfChanged();
    }

    /**
     * Whether the base is active and this turret has a target (melee turret: something in reach); synced to clients
     * (melee blades spin).
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 1.12.2 {@code AbstractDirectedTurret.update} / {@code TurretHead.updateChecks}: aiming turrets turn towards the
     * target's eyes every tick (1.12.2 turrets had no effective yaw/pitch limits; the pitch is now limited to
     * {@link TurretMath#MIN_PITCH} so the head never points into the base, and the base only picks targets above it),
     * then the weapon fires once the cooldown passed. Projectiles are shot at the predicted position (1.12.2
     * {@code ProjectileTurret.doTargetedShot}).
     */
    @SuppressWarnings("IfCanBeSwitch")
    private void engage(Entity target) {
        TurretWeapon weapon = getWeapon();
        TurretMount mount = getMount();
        Vec3 centre = mount.centre();
        if (weapon.aims()) {
            aimAt(target);
        }

        // the target's movement relative to the turret's contraption (a target not tracked yet counts as standing
        // still); the projectiles get the contraption's velocity on top (fire)
        Vec3 position = target.position();
        Vec3 moved = trackedTargetId == target.getId() && trackedTargetPos != null
                ? position.subtract(trackedTargetPos) : Vec3.ZERO;
        Vec3 relative = moved.subtract(mount.velocity());
        Vector3 velocity = new Vector3(relative.x, relative.y, relative.z);
        trackedTargetId = target.getId();
        trackedTargetPos = position;

        if (weapon instanceof ProjectileWeapon) {
            Vector3 toBody = new Vector3(target.getX() - centre.x, aimHeight(target, mount) - centre.y,
                    target.getZ() - centre.z);
            fire(toBody, velocity, target);
        } else if (weapon instanceof RayWeapon ray) {
            Optional<TurretBaseBlockEntity> base = beginShot();
            if (base.isPresent()) {
                // rays start 0.1 blocks above the turret centre, the arc is drawn from its tip
                Vec3 origin = centre.add(mount.up().scale(0.1D));
                Vec3 tip = centre.add(mount.up().scale(0.5D));
                RayWeapons.shoot(this, base.get(), ray, target, origin, tip);
            }
        } else if (weapon instanceof EffectWeapon effect) {
            beginShot().ifPresent(base -> EffectWeapons.fireEffect(this, base, effect, target));
        }
    }

    /** Turns towards the target's eyes, within {@link TurretMath#limit} (angles relative to the face, in the plot). */
    private void aimAt(Entity target) {
        TurretMount mount = getMount();
        Vec3 toEyes = mount.toLocalDirection(target.getEyePosition().subtract(mount.centre()));
        TurretMath.Angles angles = TurretMath.limit(TurretMath.anglesFor(getFace(), TurretMount.toVector(toEyes)));
        yaw = angles.yaw();
        pitch = angles.pitch();
    }

    /**
     * Height to shoot at: the body centre, or the eyes when a ledge or the like hides the centre (the base only
     * picks targets whose eyes are visible, so the shot would otherwise hit the block in front of the target).
     */
    private double aimHeight(Entity target, TurretMount mount) {
        double body = target.getY() + target.getBbHeight() * 0.5D;
        double eyes = target.getEyeY();
        if (level == null || eyes <= body || BlockEntityBaseWorld.isClear(level, mount.pose(),
                Vec3.atCenterOf(worldPosition), new Vec3(target.getX(), body, target.getZ()), target)) {
            return body;
        }
        return eyes;
    }

    /**
     * Area weapons (melee turret) ignore the base's target: the turret is active while anything it may damage is in
     * the weapon's {@linkplain AreaWeapon#area() area} and strikes it once the cooldown passed.
     *
     * @return whether anything is in reach
     */
    private boolean strikeInReach(ServerLevel level, TurretBaseBlockEntity base, AreaWeapon weapon) {
        // only entities the base may damage count (never the owner, nor trusted players by default)
        List<LivingEntity> victims = new ArrayList<>();
        AABB area = weapon.area().apply(this);
        TurretMount mount = getMount();
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (entity.isAlive() && !entity.isSpectator() && (!mount.isOnContraption() || inPlotArea(entity, area, mount))
                    && TurretDamage.canDamage(level, base.getBlockPos(), entity)) {
                victims.add(entity);
            }
        }
        updateConcealment(base.getLogic(), !victims.isEmpty());
        if (victims.isEmpty()) return false;
        beginShot().ifPresent(shooter -> EffectWeapons.fireArea(this, shooter, weapon, victims));
        return true;
    }

    /**
     * Whether an entity found for a weapon area (in the plot) really touches it: on a contraption, Sable widens the
     * search to the world area around the turned box. Like vanilla's search, it counts when the entity's bounding box
     * (taken around its centre in the plot) touches the area.
     */
    private static boolean inPlotArea(LivingEntity entity, AABB area, TurretMount mount) {
        Vec3 centre = mount.toLocal(entity.getBoundingBox().getCenter());
        return area.inflate(entity.getBbWidth() * 0.5D, entity.getBbHeight() * 0.5D, entity.getBbWidth() * 0.5D)
                .contains(centre);
    }

    /**
     * Self-targeting weapons ignore the base's target: once the turret is ready to fire, the weapon's picker finds
     * its own targets, and the turret fires if it picked anything. A pick that found nothing is retried after
     * {@link SelfTargetingWeapon#RETRY_TICKS}, so idle turrets do not scan every tick.
     *
     * @return whether the last pick found anything
     */
    private boolean pickAndFire(ServerLevel level, TurretBaseBlockEntity base, SelfTargetingWeapon weapon) {
        TurretBaseLogic logic = base.getLogic();
        TurretType type = getTurretType();
        if (ticksUntilPick > 0) {
            ticksUntilPick--;
        } else if (state.isReady(logic, type)) {
            List<Entity> targets = List.copyOf(weapon.picker().pick(level, this, base));
            picked = !targets.isEmpty();
            if (picked) {
                if (weapon.aims()) aimAt(targets.get(0));
                beginShot().ifPresent(shooter -> {
                    weapon.shot().fire(level, this, shooter, targets);
                    playSound(weapon.launchSound());
                });
            } else {
                ticksUntilPick = SelfTargetingWeapon.RETRY_TICKS;
            }
        }
        updateConcealment(logic, picked);
        return picked;
    }

    private void forgetTarget() {
        trackedTargetId = -1;
        trackedTargetPos = null;
        picked = false;
    }

    private void syncAimIfChanged() {
        if (ticksSinceSync < SYNC_INTERVAL) return;
        if (Math.abs(TurretMath.angleDifference(syncedYaw, yaw)) > SYNC_THRESHOLD_DEGREES
                || Math.abs(pitch - syncedPitch) > SYNC_THRESHOLD_DEGREES) {
            syncedYaw = yaw;
            syncedPitch = pitch;
            ticksSinceSync = 0;
            syncToClient();
        }
    }

    /** Applies {@link TurretState#updateConcealment}: a retracted turret has no shape. */
    private void updateConcealment(TurretBaseLogic logic, boolean hasTarget) {
        boolean concealed = getBlockState().getValue(TurretBlock.CONCEALED);
        switch (state.updateConcealment(logic.isConcealingTurrets(), concealed, hasTarget)) {
            case DEPLOY -> {
                playSound(ModSounds.TURRET_DEPLOY);
                setConcealed(false);
            }
            case RETRACT -> {
                playSound(ModSounds.TURRET_RETRACT);
                setConcealed(true);
            }
            case REVEAL -> setConcealed(false);
            case NONE -> {
            }
        }
    }

    private void setConcealed(boolean concealed) {
        if (level != null && getBlockState().getValue(TurretBlock.CONCEALED) != concealed) {
            level.setBlock(worldPosition, getBlockState().setValue(TurretBlock.CONCEALED, concealed), 3);
        }
    }

    /**
     * Starts a shot ({@link TurretState#beginShot}) and keeps its ammo in {@link #lastAmmo}.
     *
     * @return the base, or empty if the turret cannot shoot now (nothing was consumed)
     */
    private Optional<TurretBaseBlockEntity> beginShot() {
        lastAmmo = null;
        Optional<TurretBaseBlockEntity> base = getBase();
        if (base.isEmpty()) return Optional.empty();
        Optional<TurretState.Shot> shot = state.beginShot(base.get().getLogic(), getTurretType());
        if (shot.isEmpty()) return Optional.empty();
        lastAmmo = shot.get().ammo().orElse(null);
        return base;
    }

    /**
     * Fires projectiles in {@code direction} (1.12.2 {@code ProjectileTurret.shootProjectile}): one per scattershot
     * level, with the turret's inaccuracy.
     *
     * @param toAim    aim point minus turret centre ({@link TurretMount#centre()}), in the world
     * @param velocity target movement per tick relative to the turret's contraption (the projectiles get the
     *                 contraption's velocity on top)
     * @return whether a shot was fired
     */
    public boolean fire(Vector3 toAim, Vector3 velocity, @Nullable Entity target) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        if (!(getWeapon() instanceof ProjectileWeapon weapon)) return false;
        Optional<TurretBaseBlockEntity> base = beginShot();
        if (base.isEmpty()) return false;
        TurretBaseLogic logic = base.get().getLogic();
        TurretType type = getTurretType();
        float inaccuracy = ProjectileMath.inaccuracy(logic.getAccuracyDeviation(type));
        TurretMount mount = getMount();
        Vec3 origin = mount.centre();
        for (int i = 0; i <= logic.getScattershotCount(type); i++) {
            TurretProjectile projectile = weapon.factory().create(serverLevel, lastAmmo, target);
            if (projectile == null) continue;
            projectile.setBase(base.get());
            projectile.setHitHandler(weapon.hitHandler());
            projectile.setPos(origin);
            // the lead depends on the projectile's gravity, so it is worked out per projectile
            TurretMath.Lead lead = TurretMath.lead(toAim, velocity, weapon.speed(), projectile.getGravityEffect());
            Vector3 direction = lead.direction();
            projectile.shoot(direction.x(), direction.y(), direction.z(), weapon.speed() * lead.speedFactor(), inaccuracy);
            if (mount.isOnContraption()) {
                projectile.setDeltaMovement(projectile.getDeltaMovement().add(mount.velocity()));
            }
            if (logic.getDamageAmpLevel() > 0) {
                playSound(ModSounds.AMPED);
            }
            playSound(weapon.launchSound());
            serverLevel.addFreshEntity(projectile);
        }
        return true;
    }

    private void playSound(SoundEvent sound) {
        if (level != null) {
            level.playSound(null, worldPosition, sound, SoundSource.BLOCKS,
                    (float) OMTConfig.get().turrets.turretSoundVolume, level.getRandom().nextFloat() + 0.5F);
        }
    }

    // ---- Client ----

    /** Turns the rendered model smoothly towards the synced aim and spins it while active. */
    @SuppressWarnings("unused")
    public static void clientTick(Level level, BlockPos pos, BlockState state, TurretBlockEntity turret) {
        turret.prevRenderYaw = turret.renderYaw;
        turret.prevRenderPitch = turret.renderPitch;
        turret.renderYaw += TurretMath.angleDifference(turret.renderYaw, turret.yaw) * CLIENT_TURN_RATE;
        turret.renderPitch += (turret.pitch - turret.renderPitch) * CLIENT_TURN_RATE;
        // kept in [0, 360) so the interpolation never jumps
        turret.renderSpin %= 360.0F;
        turret.prevRenderSpin = turret.renderSpin;
        if (turret.active) {
            turret.renderSpin += CLIENT_SPIN_RATE;
        }
    }

    /** Interpolated spin angle of the melee turret's top piece, in degrees. */
    public float getRenderSpin(float partialTick) {
        return prevRenderSpin + (renderSpin - prevRenderSpin) * partialTick;
    }

    /** Interpolated yaw for rendering. */
    public float getRenderYaw(float partialTick) {
        return prevRenderYaw + TurretMath.angleDifference(prevRenderYaw, renderYaw) * partialTick;
    }

    /** Interpolated pitch for rendering. */
    public float getRenderPitch(float partialTick) {
        return prevRenderPitch + (renderPitch - prevRenderPitch) * partialTick;
    }

    // ---- NBT ----

    @Override
    protected void readData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("TicksSinceShot")) {
            state.setTicksSinceShot(NbtCompat.getInt(tag, "TicksSinceShot", 0));
        }
        yaw = NbtCompat.getFloat(tag, "Yaw", 0);
        pitch = NbtCompat.getFloat(tag, "Pitch", 0);
        active = NbtCompat.getBoolean(tag, "Active", false);
        if (!renderInitialised) {
            renderYaw = prevRenderYaw = yaw;
            renderPitch = prevRenderPitch = pitch;
            renderInitialised = true;
        }
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        writeClientData(tag, registries);
        tag.putInt("TicksSinceShot", state.getTicksSinceShot());
    }

    /** Clients only need the aim and whether the turret is active. */
    @Override
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);
        tag.putBoolean("Active", active);
    }
}

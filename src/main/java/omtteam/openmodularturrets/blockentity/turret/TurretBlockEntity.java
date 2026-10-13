package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.blockentity.base.LivingTargetEntity;
import omtteam.openmodularturrets.compat.CompatBlockEntity;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.target.TargetEntity;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import omtteam.openmodularturrets.entity.projectile.TurretProjectile;
import omtteam.openmodularturrets.registry.ModBlockEntities;
import omtteam.openmodularturrets.registry.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A turret (1.12.2 {@code TurretHead} / {@code AbstractDirectedTurret} / {@code ProjectileTurret}). Thin by design: the
 * base owns targeting, ammo, energy and upgrades; the turret aims at the base's target, keeps its cooldown and
 * fires. The weapon (projectile or ray) comes from {@link TurretWeapon}.
 */
public class TurretBlockEntity extends CompatBlockEntity {
    /** 1.12.2 {@code concealmentChecks}: retract after 40 ticks without a target. */
    private static final int CONCEAL_AFTER_TICKS = 40;
    /** 1.12.2 {@code AbstractDirectedTurret.update}: the aim was sent to clients every 5 ticks. */
    private static final int SYNC_INTERVAL = 5;
    /** Smaller aim changes are not synced. */
    private static final float SYNC_THRESHOLD_DEGREES = 0.5F;
    /** Share of the remaining angle the client model turns per tick. */
    private static final float CLIENT_TURN_RATE = 0.5F;

    /** Ticks since the last shot (1.12.2 {@code ticks}). */
    private int ticksSinceShot;
    private int ticksWithoutTarget;
    private boolean playedDeploy;

    /** Aim relative to the mounting face (see {@link TurretMath}), in degrees; synced to clients. */
    private float yaw;
    private float pitch;
    private float syncedYaw;
    private float syncedPitch;
    private int ticksSinceSync;

    /** Ammo taken by the last {@link #beginShot()}, if the turret uses ammo. */
    @Nullable
    private AmmoDraw lastAmmo;

    /** Target tracking for the lead (1.12.2 tracked players manually, others via prevPos). */
    private int trackedTargetId = -1;
    @Nullable
    private Vec3 trackedTargetPos;

    /** Client only: smoothed render angles. */
    private float renderYaw;
    private float renderPitch;
    private float prevRenderYaw;
    private float prevRenderPitch;
    private boolean renderInitialised;

    public TurretBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TURRET, pos, state);
    }

    public TurretType getTurretType() {
        return ((TurretBlock) getBlockState().getBlock()).getTurretType();
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    // ---- Server ----

    public static void serverTick(Level level, BlockPos pos, BlockState state, TurretBlockEntity turret) {
        BlockPos basePos = turret.getBasePos();
        if (!level.isLoaded(basePos)) return;
        Optional<TurretBaseBlockEntity> base = turret.getBase();
        TurretType type = turret.getTurretType();
        // a turret without base, or on a base of a lower tier, drops itself
        if (base.isEmpty() || !base.get().getLogic().supportsTurret(type)) {
            level.destroyBlock(pos, true);
            return;
        }
        turret.ticksSinceShot++;
        turret.ticksSinceSync++;
        TurretBaseLogic logic = base.get().getLogic();
        if (logic.isActive()) {
            Optional<Entity> target = logic.getTarget(turret.getFace())
                    .filter(LivingTargetEntity.class::isInstance)
                    .map(t -> (Entity) ((LivingTargetEntity) t).getEntity());
            turret.updateConcealment(logic, target.isPresent());
            target.ifPresentOrElse(turret::engage, turret::forgetTarget);
        } else {
            turret.forgetTarget();
        }
        turret.syncAimIfChanged();
    }

    /**
     * 1.12.2 {@code AbstractDirectedTurret.update} / {@code TurretHead.updateChecks}: aiming turrets turn towards the
     * target's eyes every tick (1.12.2 turrets had no effective yaw/pitch limits; the pitch is now limited to
     * {@link TurretMath#MIN_PITCH} so the head never points into the base, and the base only picks targets above it),
     * then the weapon fires once the cooldown passed. Projectiles are shot at the predicted position (1.12.2
     * {@code ProjectileTurret.doTargetedShot}).
     */
    private void engage(Entity target) {
        TurretWeapon weapon = TurretWeapon.of(getTurretType());
        Vec3 centre = Vec3.atCenterOf(worldPosition);
        if (weapon.kind().aims()) {
            Vector3 toEyes = new Vector3(target.getX() - centre.x, target.getEyeY() - centre.y, target.getZ() - centre.z);
            TurretMath.Angles angles = TurretMath.limit(TurretMath.anglesFor(getFace(), toEyes));
            yaw = angles.yaw();
            pitch = angles.pitch();
        }

        Vec3 position = target.position();
        Vector3 velocity = trackedTargetId == target.getId() && trackedTargetPos != null
                ? new Vector3(position.x - trackedTargetPos.x, position.y - trackedTargetPos.y, position.z - trackedTargetPos.z)
                : Vector3.ZERO;
        trackedTargetId = target.getId();
        trackedTargetPos = position;

        switch (weapon.kind()) {
            case PROJECTILE -> {
                Vector3 toBody = new Vector3(target.getX() - centre.x, target.getY() + target.getBbHeight() * 0.5D - centre.y,
                        target.getZ() - centre.z);
                TurretMath.Lead lead = TurretMath.lead(toBody, velocity, weapon.leadWithGravity());
                Vector3 direction = lead.direction();
                fire(new Vec3(direction.x(), direction.y(), direction.z()), lead.speedFactor(), target);
            }
            case RAY -> {
                Optional<TurretBaseBlockEntity> base = beginShot();
                if (base.isPresent()) {
                    // rays start 0.1 blocks above the turret centre, the arc is drawn from its tip
                    Vector3 up = Vector3.of(getFace());
                    Vec3 origin = centre.add(up.x() * 0.1D, up.y() * 0.1D, up.z() * 0.1D);
                    Vec3 tip = centre.add(up.x() * 0.5D, up.y() * 0.5D, up.z() * 0.5D);
                    RayWeapons.shoot(this, base.get(), target, origin, tip);
                }
            }
            case EFFECT -> beginShot().ifPresent(base -> EffectWeapons.fire(this, base, target));
        }
    }

    private void forgetTarget() {
        trackedTargetId = -1;
        trackedTargetPos = null;
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

    /**
     * 1.12.2 {@code TurretHead.concealmentChecks}: with a concealer addon the turret retracts (no shape, retract
     * sound) after 40 ticks without a target and deploys again as soon as it has one.
     */
    private void updateConcealment(TurretBaseLogic logic, boolean hasTarget) {
        boolean concealed = getBlockState().getValue(TurretBlock.CONCEALED);
        if (!logic.isConcealingTurrets()) {
            if (concealed) setConcealed(false);
            return;
        }
        if (hasTarget) {
            ticksWithoutTarget = 0;
            if (!playedDeploy) {
                playSound(ModSounds.TURRET_DEPLOY);
                playedDeploy = true;
                setConcealed(false);
            }
        } else if (!concealed && ticksWithoutTarget >= CONCEAL_AFTER_TICKS) {
            ticksWithoutTarget = 0;
            playedDeploy = false;
            playSound(ModSounds.TURRET_RETRACT);
            setConcealed(true);
        } else {
            ticksWithoutTarget++;
        }
    }

    private void setConcealed(boolean concealed) {
        if (level != null && getBlockState().getValue(TurretBlock.CONCEALED) != concealed) {
            level.setBlock(worldPosition, getBlockState().setValue(TurretBlock.CONCEALED, concealed), 3);
        }
    }

    /**
     * Common part of every shot (1.12.2 {@code updateChecks} / {@code AbstractDirectedTurret.update}): the cooldown
     * must have passed and the base must be active with enough energy; ammo turrets take {@code 1 + scattershot}
     * items. Then the energy is consumed and the cooldown restarts.
     *
     * @return the base, or empty if the turret cannot shoot now (nothing was consumed)
     */
    private Optional<TurretBaseBlockEntity> beginShot() {
        lastAmmo = null;
        Optional<TurretBaseBlockEntity> base = getBase();
        if (base.isEmpty()) return Optional.empty();
        TurretBaseLogic logic = base.get().getLogic();
        TurretType type = getTurretType();
        if (ticksSinceShot < logic.getFireCooldownTicks(type) || !logic.isActive() || !logic.hasEnergyForShot(type)) {
            return Optional.empty();
        }
        if (logic.getRequiredAmmo(type).isPresent()) {
            Optional<AmmoDraw> drawn = logic.drawAmmo(type);
            if (drawn.isEmpty()) return Optional.empty(); // 1.12.2: "TODO: Play some kind of clicking sound?"
            lastAmmo = drawn.get();
        }
        logic.consumeShotEnergy(type);
        ticksSinceShot = 0;
        return base;
    }

    /**
     * Fires projectiles in {@code direction} (1.12.2 {@code ProjectileTurret.shootProjectile}): one per scattershot
     * level, with the turret's inaccuracy.
     *
     * @param speedFactor lead correction from {@link TurretMath#lead} (1 = no correction)
     * @return whether a shot was fired
     */
    public boolean fire(Vec3 direction, float speedFactor, @Nullable Entity target) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        TurretWeapon weapon = TurretWeapon.of(getTurretType());
        if (weapon.kind() != TurretWeapon.Kind.PROJECTILE) return false;
        Optional<TurretBaseBlockEntity> base = beginShot();
        if (base.isEmpty()) return false;
        TurretBaseLogic logic = base.get().getLogic();
        TurretType type = getTurretType();
        float inaccuracy = (float) (logic.getAccuracyDeviation(type) / 20D);
        Vec3 origin = Vec3.atCenterOf(worldPosition);
        for (int i = 0; i <= logic.getScattershotCount(); i++) {
            TurretProjectile projectile = weapon.factory().create(serverLevel, lastAmmo, target);
            if (projectile == null) continue;
            projectile.setBase(base.get());
            projectile.setPos(origin);
            projectile.shoot(direction.x, direction.y, direction.z, weapon.speed() * speedFactor, inaccuracy);
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

    /** Turns the rendered model smoothly towards the synced aim. */
    public static void clientTick(Level level, BlockPos pos, BlockState state, TurretBlockEntity turret) {
        turret.prevRenderYaw = turret.renderYaw;
        turret.prevRenderPitch = turret.renderPitch;
        turret.renderYaw += TurretMath.angleDifference(turret.renderYaw, turret.yaw) * CLIENT_TURN_RATE;
        turret.renderPitch += (turret.pitch - turret.renderPitch) * CLIENT_TURN_RATE;
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
            ticksSinceShot = tag.getInt("TicksSinceShot");
        }
        yaw = tag.getFloat("Yaw");
        pitch = tag.getFloat("Pitch");
        if (!renderInitialised) {
            renderYaw = prevRenderYaw = yaw;
            renderPitch = prevRenderPitch = pitch;
            renderInitialised = true;
        }
    }

    @Override
    protected void writeData(CompoundTag tag, HolderLookup.Provider registries) {
        writeClientData(tag, registries);
        tag.putInt("TicksSinceShot", ticksSinceShot);
    }

    /** Clients only need the aim. */
    @Override
    protected void writeClientData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);
    }
}

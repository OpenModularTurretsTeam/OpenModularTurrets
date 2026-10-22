package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;

import java.util.Optional;

/**
 * A turret's own state: its fire cooldown and the concealer addon's retract / deploy cycle (1.12.2
 * {@code TurretHead.updateChecks} and {@code concealmentChecks}). The turret block entity ticks it and applies the
 * results (firing, sounds, block state).
 */
public final class TurretState {
    /** 1.12.2 {@code concealmentChecks}: retract after 40 ticks without a target. */
    public static final int CONCEAL_AFTER_TICKS = 40;

    /** What the turret has to do about its concealment this tick. */
    public enum Concealment {
        /** Nothing changes. */
        NONE,
        /** Retract into the base, with the retract sound. */
        RETRACT,
        /** Come out of the base, with the deploy sound. */
        DEPLOY,
        /** Come out without a sound: the base has no concealer addon (any more). */
        REVEAL
    }

    /** A shot that was started: its energy is already consumed (a record in 1.20.1+). */
    public static final class Shot {
        private final Optional<AmmoDraw> ammo;

        /** @param ammo the ammo taken for it, empty for turrets without ammo */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public Shot(Optional<AmmoDraw> ammo) {
            this.ammo = ammo;
        }

        /** The ammo taken for the shot, empty for turrets without ammo. */
        public Optional<AmmoDraw> ammo() {
            return ammo;
        }
    }

    /** Ticks since the last shot (1.12.2 {@code ticks}). */
    private int ticksSinceShot;
    private int ticksWithoutTarget;
    private boolean playedDeploy;

    /** Called every server tick while the turret has a base. */
    public void tick() {
        ticksSinceShot++;
    }

    /** Ticks since the last shot began; compared with the fire cooldown. */
    public int getTicksSinceShot() {
        return ticksSinceShot;
    }

    /** Restores the cooldown from saved data. */
    public void setTicksSinceShot(int ticksSinceShot) {
        this.ticksSinceShot = ticksSinceShot;
    }

    /**
     * Whether {@link #beginShot} could start a shot apart from the ammo: the cooldown passed and the base is active
     * with enough energy. Nothing is consumed.
     */
    public boolean isReady(TurretBaseLogic logic, TurretType type) {
        return ticksSinceShot >= logic.getFireCooldownTicks(type) && logic.isActive() && logic.hasEnergyForShot(type);
    }

    /**
     * Common part of every shot (1.12.2 {@code updateChecks} / {@code AbstractDirectedTurret.update}): the cooldown
     * must have passed and the base must be active with enough energy; ammo turrets take their ammo
     * ({@link TurretBaseLogic#drawAmmo}). Then the energy is consumed and the cooldown restarts.
     *
     * @return the shot, or empty if the turret cannot shoot now (nothing was consumed)
     */
    public Optional<Shot> beginShot(TurretBaseLogic logic, TurretType type) {
        if (!isReady(logic, type)) return Optional.empty();
        Optional<AmmoDraw> ammo = Optional.empty();
        if (logic.getRequiredAmmo(type).isPresent()) {
            ammo = logic.drawAmmo(type);
            if (!ammo.isPresent()) return Optional.empty();
        }
        logic.consumeShotEnergy(type);
        ticksSinceShot = 0;
        return Optional.of(new Shot(ammo));
    }

    /**
     * 1.12.2 {@code TurretHead.concealmentChecks}, called every tick the base is active: with a concealer addon the
     * turret retracts after {@link #CONCEAL_AFTER_TICKS} ticks without a target and deploys again as soon as it has
     * one. Without a concealer a retracted turret comes out silently.
     *
     * @param concealing whether the base conceals its turrets
     * @param concealed  whether the turret is retracted now
     */
    public Concealment updateConcealment(boolean concealing, boolean concealed, boolean hasTarget) {
        if (!concealing) {
            return concealed ? Concealment.REVEAL : Concealment.NONE;
        }
        if (hasTarget) {
            ticksWithoutTarget = 0;
            if (playedDeploy) return Concealment.NONE;
            playedDeploy = true;
            return Concealment.DEPLOY;
        }
        if (!concealed && ticksWithoutTarget >= CONCEAL_AFTER_TICKS) {
            ticksWithoutTarget = 0;
            playedDeploy = false;
            return Concealment.RETRACT;
        }
        ticksWithoutTarget++;
        return Concealment.NONE;
    }
}

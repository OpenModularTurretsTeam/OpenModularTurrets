package omtteam.openmodularturrets.core.target;

import omtteam.openmodularturrets.core.base.TargetingSettings;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.TrustManager;
import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.BaseWorld;
import omtteam.openmodularturrets.core.world.BlockPosition;
import omtteam.openmodularturrets.core.world.Box;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Decides which entities a turret may target and which one it prefers. Ported from oldmod
 * {@code TurretTargetingUtils} and {@code TurretHead.getTarget}.
 */
public final class TargetSelector {
    /** How the config lists and the default category classify an entity. */
    public enum Classification {
        BLOCKED, HOSTILE, NEUTRAL, NONE
    }

    private final LogicSettings settings;
    private final TrustManager trust;
    private final BaseWorld world;

    public TargetSelector(LogicSettings settings, TrustManager trust, BaseWorld world) {
        this.settings = settings;
        this.trust = trust;
        this.world = world;
    }

    /**
     * Classification in the order block list → neutral force list → mob force list → default category, so the
     * neutral force list can protect passive mod entities that are wrongly classified as monsters. (oldmod never
     * used the block list and let a monster on the neutral list stay hostile.)
     */
    public Classification classify(TargetEntity entity) {
        String type = entity.getTypeId();
        if (settings.blockedEntities().contains(type)) return Classification.BLOCKED;
        if (settings.forcedNeutralEntities().contains(type)) return Classification.NEUTRAL;
        if (settings.forcedHostileEntities().contains(type)) return Classification.HOSTILE;
        return switch (entity.getCategory()) {
            case MONSTER -> Classification.HOSTILE;
            case PASSIVE -> Classification.NEUTRAL;
            case OTHER -> Classification.NONE;
        };
    }

    /**
     * The best target for a turret (oldmod {@code getTarget} + {@code getBestEntity}): candidates in the cube
     * {@code turret ± (range + 1)}, filtered by {@link #isValidTarget}, highest {@link TargetingPriority#score} wins
     * (first one on ties).
     *
     * @param takenByOthers entity ids targeted by the base's other turrets (only excluded when multi-targeting)
     */
    public Optional<TargetEntity> findBest(TurretType type, BlockPosition turret, TargetingSettings targeting,
                                           boolean multiTargeting, Collection<Integer> takenByOthers) {
        List<TargetEntity> candidates = world.getLivingEntities(Box.around(turret, targeting.getRange() + 1));
        int[] weights = type.getDefaultPriorities();
        TargetEntity best = null;
        long bestScore = Long.MIN_VALUE;
        for (TargetEntity entity : candidates) {
            if (multiTargeting && takenByOthers.contains(entity.getId())) continue;
            if (!isValidTarget(entity, type, turret, targeting)) continue;
            long score = TargetingPriority.score(entity, turret, weights);
            if (score > bestScore) {
                bestScore = score;
                best = entity;
            }
        }
        return Optional.ofNullable(best);
    }

    /**
     * oldmod {@code validTarget} (without the multi-targeting check, see {@link #findBest}).
     */
    public boolean isValidTarget(TargetEntity entity, TurretType type, BlockPosition turret, TargetingSettings targeting) {
        if (!entity.isAlive() || entity.getHealth() <= 0) return false;
        Classification classification = classify(entity);
        if (classification == Classification.BLOCKED) return false;

        Optional<PlayerInfo> player = entity.getPlayer();
        if (player.isPresent()) {
            if (!targeting.isTargetPlayers() || !settings.globalCanTargetPlayers()) return false;
            if (trust.isProtectedFromTargeting(player.get())) return false;
        }
        if (!isInRange(entity, turret, targeting.getRange())) return false;
        if (!canSee(entity, turret)) return false;

        Optional<PlayerIdentity> owner = entity.getOwner();
        if (owner.isPresent() && trust.isOwnerOrTrusted(owner.get())) return false;
        if (owner.isEmpty() && entity.isTamedHorse()) return false;

        // oldmod RelativisticTurretTileEntity.isEntityValidTarget: slowed entities are already "handled"
        if (type == TurretType.RELATIVISTIC_TURRET && entity.hasSlowness()) return false;

        if (classification == Classification.NEUTRAL && targeting.isTargetPassive() && settings.globalCanTargetNeutrals()) {
            return true;
        }
        if (classification == Classification.HOSTILE && targeting.isTargetMobs() && settings.globalCanTargetMobs()) {
            return true;
        }
        return player.isPresent();
    }

    /**
     * oldmod {@code chebyshevDistance}: the largest axis distance between the turret's block corner and the
     * entity's position must not exceed the base range.
     */
    public static boolean isInRange(TargetEntity entity, BlockPosition turret, int range) {
        double distance = Math.max(Math.max(Math.abs(entity.getX() - turret.x()), Math.abs(entity.getY() - turret.y())),
                Math.abs(entity.getZ() - turret.z()));
        return distance <= range;
    }

    /** oldmod {@code canSeeTargetFromPos}: from the turret's centre to the entity's eyes. */
    public boolean canSee(TargetEntity entity, BlockPosition turret) {
        return world.hasLineOfSight(turret.x() + 0.5, turret.y() + 0.5, turret.z() + 0.5, entity);
    }
}

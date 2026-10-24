package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.settings.LogicSettings;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;

import java.util.Map;

/**
 * Whether a turret may be attached to a face of a base, or stay there (1.12.2 {@code canPlaceBlockAt} and
 * {@code AbstractDirectedTurret.update}; 1.20.1+ {@code TurretPlacement}). Pure rules on the given state, so the client
 * (placement outline and its reason) and the server (placing, ticking) get the same answer from what both can see: the
 * config, the base tier and the turrets around the base.
 */
public final class TurretPlacement {
    /** Why a turret may or may not be attached; the checks run in this order and the first failing one wins. */
    public enum Result {
        OK,
        /** The turret type is disabled in the config. */
        DISABLED,
        /** The type cannot mount on this face of the base ({@link TurretType#canMountOn}). */
        WRONG_FACE,
        /** The base's tier is below the type's {@link TurretType#getRequiredBaseTier}. */
        TIER_TOO_LOW,
        /** The base tier's {@code maxTurrets} are all taken. */
        BASE_FULL,
        /** The base already holds {@code maxSimultaneous} turrets of this type. */
        TYPE_LIMIT;

        public boolean allowed() {
            return this == OK;
        }
    }

    private TurretPlacement() {
    }

    /**
     * May a turret of this type be added on this face?
     *
     * @param attached the turrets on the base by face; a turret on {@code face} itself is not counted
     */
    public static Result canAttach(TurretType type, Face face, int baseTier, Map<Face, TurretType> attached,
                                   LogicSettings settings) {
        if (!settings.turret(type).enabled()) return Result.DISABLED;
        Result mount = canStay(type, face, baseTier);
        if (!mount.allowed()) return mount;
        long others = attached.keySet().stream().filter(other -> other != face).count();
        if (others >= settings.baseTier(baseTier).maxTurrets()) return Result.BASE_FULL;
        if (countOfType(type, attached, face) >= settings.turret(type).maxSimultaneous()) return Result.TYPE_LIMIT;
        return Result.OK;
    }

    /**
     * May an attached turret stay? Checks the face and the tier; disabling a type or lowering the base's
     * {@code maxTurrets} or the type's {@code maxSimultaneous} never removes turrets.
     */
    public static Result canStay(TurretType type, Face face, int baseTier) {
        if (!type.canMountOn(face)) return Result.WRONG_FACE;
        if (baseTier < type.getRequiredBaseTier()) return Result.TIER_TOO_LOW;
        return Result.OK;
    }

    /** Turrets of {@code type} in {@code attached}, not counting the one on {@code except}. */
    private static long countOfType(TurretType type, Map<Face, TurretType> attached, Face except) {
        return attached.entrySet().stream()
                .filter(entry -> entry.getKey() != except && type.equals(entry.getValue()))
                .count();
    }
}

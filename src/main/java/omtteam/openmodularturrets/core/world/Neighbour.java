package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.turret.TurretType;

/**
 * Something attached to one face of a base that the base logic cares about.
 */
public sealed interface Neighbour permits Neighbour.Turret, Neighbour.Expander {
    /** A turret mounted on this face. */
    record Turret(TurretType type) implements Neighbour {
    }

    /**
     * An inventory or power expander (1.12.2 {@code Expander}). Its slots are provided through
     * {@link BaseWorld#getAmmoSlots()}.
     *
     * @param tier 1–5
     */
    record Expander(boolean power, int tier) implements Neighbour {
    }
}

package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.turret.TurretType;

/**
 * Something attached to one face of a base that the base logic cares about.
 */
public sealed interface Neighbour permits Neighbour.Turret {
    /** A turret mounted on this face. */
    record Turret(TurretType type) implements Neighbour {
    }
}

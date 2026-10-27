package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.List;

/**
 * Something attached to one face of a base that the base logic cares about.
 */
public sealed interface Neighbour permits Neighbour.Turret, Neighbour.Expanders {
    /** A turret mounted on this face. */
    record Turret(TurretType type) implements Neighbour {
    }

    /**
     * The plates of the inventory expander on this face: 1 to {@value SlotCapacity#MAX_STACKED}
     * tiers (1–{@value SlotCapacity#MAX_TIER}).
     */
    record Expanders(List<Integer> tiers) implements Neighbour {
        public Expanders {
            tiers = List.copyOf(tiers);
            if (tiers.isEmpty() || tiers.size() > SlotCapacity.MAX_STACKED) {
                throw new IllegalArgumentException("Expected 1 to " + SlotCapacity.MAX_STACKED + " expanders: " + tiers);
            }
            for (int tier : tiers) {
                if (tier < 1 || tier > SlotCapacity.MAX_TIER) {
                    throw new IllegalArgumentException("Invalid expander tier: " + tier);
                }
            }
        }
    }
}

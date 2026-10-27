package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.storage.SlotCapacity;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Something attached to one face of a base that the base logic cares about (a sealed interface in 1.20.1+).
 */
public interface Neighbour {
    /** A turret mounted on this face. */
    final class Turret implements Neighbour {
        private final TurretType type;

        public Turret(TurretType type) {
            this.type = Objects.requireNonNull(type);
        }

        public TurretType type() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Turret && ((Turret) o).type == type;
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }

        @Override
        public String toString() {
            return "Turret[" + type + "]";
        }
    }

    /**
     * The plates of the inventory expander on this face: 1 to {@value SlotCapacity#MAX_STACKED}
     * tiers (1–{@value SlotCapacity#MAX_TIER}).
     */
    final class Expanders implements Neighbour {
        private final List<Integer> tiers;

        public Expanders(List<Integer> tiers) {
            List<Integer> copy = Collections.unmodifiableList(new ArrayList<>(tiers));
            if (copy.isEmpty() || copy.size() > SlotCapacity.MAX_STACKED) {
                throw new IllegalArgumentException("Expected 1 to " + SlotCapacity.MAX_STACKED + " expanders: " + copy);
            }
            for (Integer tier : copy) {
                if (tier == null || tier < 1 || tier > SlotCapacity.MAX_TIER) {
                    throw new IllegalArgumentException("Invalid expander tier: " + tier);
                }
            }
            this.tiers = copy;
        }

        public List<Integer> tiers() {
            return tiers;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Expanders && ((Expanders) o).tiers.equals(tiers);
        }

        @Override
        public int hashCode() {
            return tiers.hashCode();
        }

        @Override
        public String toString() {
            return "Expanders" + tiers;
        }
    }
}

package omtteam.openmodularturrets.core.world;

import omtteam.openmodularturrets.core.turret.TurretType;

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
}

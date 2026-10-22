package omtteam.openmodularturrets.core.security;

import omtteam.omlib.core.security.PlayerIdentity;
import omtteam.omlib.core.security.TrustManager;

import java.util.Objects;
import java.util.Optional;

/**
 * Who turret damage may hit (1.12.2 {@code OMTUtil.canDamageEntity}), for projectiles, rays and the melee turret
 * alike. The game side says what kind of {@link Victim} an entity is and looks up the base's trust list.
 */
public final class DamageRules {
    private DamageRules() {
    }

    /** What a turret is about to hurt (a sealed interface with records in 1.20.1+). */
    public interface Victim {
        /** Another turret projectile (grenades, plasma, ...): never hit. */
        final class Projectile implements Victim {
        }

        /** A player; hit only if the base's trust rules allow it. */
        final class Player implements Victim {
            private final PlayerIdentity identity;

            public Player(PlayerIdentity identity) {
                this.identity = Objects.requireNonNull(identity);
            }

            public PlayerIdentity identity() {
                return identity;
            }
        }

        /** A tamed animal (wolf, cat, ...) of a player. */
        final class Pet implements Victim {
            private final PlayerIdentity owner;

            public Pet(PlayerIdentity owner) {
                this.owner = Objects.requireNonNull(owner);
            }

            public PlayerIdentity owner() {
                return owner;
            }
        }

        /** Mobs, animals without owner and any other entity. */
        final class Other implements Victim {
        }
    }

    /**
     * Turret projectiles are never hit. Players and pets of players are hit only if the base's trust rules allow it
     * ({@link TrustManager#canDamagePlayer}, for a pet the rules of its owner). Without a loaded base ({@code trust}
     * empty) players and pets are spared and everything else is hit.
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static boolean canDamage(Optional<TrustManager> trust, Victim victim) {
        if (victim instanceof Victim.Projectile) return false;
        if (victim instanceof Victim.Player) {
            PlayerIdentity player = ((Victim.Player) victim).identity();
            return trust.map(t -> t.canDamagePlayer(player)).orElse(false);
        }
        if (victim instanceof Victim.Pet) {
            PlayerIdentity owner = ((Victim.Pet) victim).owner();
            return trust.map(t -> t.canDamagePlayer(owner)).orElse(false);
        }
        return true;
    }
}

package omtteam.openmodularturrets.core.security;

import java.util.Optional;

/**
 * Who turret damage may hit (1.12.2 {@code OMTUtil.canDamageEntity}), for projectiles, rays and the melee turret
 * alike. The game side says what kind of {@link Victim} an entity is and looks up the base's trust list.
 */
public final class DamageRules {
    private DamageRules() {
    }

    /** What a turret is about to hurt. */
    public sealed interface Victim {
        /** Another turret projectile (grenades, plasma, ...): never hit. */
        record Projectile() implements Victim {
        }

        record Player(PlayerIdentity identity) implements Victim {
        }

        /** A tamed animal (wolf, cat, ...) of a player. */
        record Pet(PlayerIdentity owner) implements Victim {
        }

        /** Mobs, animals without owner and any other entity. */
        record Other() implements Victim {
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
        if (victim instanceof Victim.Player player) {
            return trust.map(t -> t.canDamagePlayer(player.identity())).orElse(false);
        }
        if (victim instanceof Victim.Pet pet) {
            return trust.map(t -> t.canDamagePlayer(pet.owner())).orElse(false);
        }
        return true;
    }
}

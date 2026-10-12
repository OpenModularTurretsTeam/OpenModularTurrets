package omtteam.openmodularturrets.core.security;

import java.util.Optional;
import java.util.UUID;

/**
 * What the permission system needs to know about the world and the config. Implemented by the Minecraft layer.
 */
public interface SecurityEnvironment {
    /**
     * The scoreboard team of a player, looked up by name (works for offline players too).
     */
    Optional<String> getTeam(UUID uuid, String name);

    /** OMLib config {@code canOPAccessOwnedBlocks}: operators may open every base and are never targeted. */
    boolean operatorsCanAccessOwnedBlocks();

    /** Config {@code turrets.turretDamageTrustedPlayers}: trusted players are hurt when accidentally hit. */
    boolean turretsDamageTrustedPlayers();
}

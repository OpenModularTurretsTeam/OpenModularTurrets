package omtteam.openmodularturrets.core.computer;

import omtteam.omlib.core.security.PlayerIdentity;

import java.util.Optional;

/**
 * Resolves a player name for the computer commands that add trusted players. Implemented by the Minecraft layer.
 */
@FunctionalInterface
public interface PlayerNames {
    /** Online or cached profile; never a network request. */
    Optional<PlayerIdentity> find(String name);
}

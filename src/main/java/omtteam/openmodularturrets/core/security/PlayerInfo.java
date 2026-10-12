package omtteam.openmodularturrets.core.security;

import java.util.UUID;

/**
 * Snapshot of an online player that wants to do something with a base. Created by the Minecraft layer.
 *
 * @param operator whether the player has operator permission level 4 (OMLib {@code PlayerUtil.isPlayerOP})
 */
public record PlayerInfo(UUID uuid, String name, boolean operator) {
    public PlayerIdentity identity() {
        return new PlayerIdentity(uuid, name);
    }
}

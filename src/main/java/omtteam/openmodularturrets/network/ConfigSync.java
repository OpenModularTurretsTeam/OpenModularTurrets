package omtteam.openmodularturrets.network;

import omtteam.omlib.network.ConfigSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import omtteam.omlib.compat.VersionCompat;
import omtteam.omlib.compat.network.NetworkCompat;
import omtteam.openmodularturrets.config.OMTConfig;

/**
 * Sends the server's config to joining players. Kept out of {@code config}, so the config does not depend on the
 * packets.
 */
public final class ConfigSync {
    private ConfigSync() {
    }

    /**
     * Sends the server's config to a joining player; called by the loader's player-join event on the server.
     */
    public static void onPlayerJoin(ServerPlayer player) {
        // The singleplayer host already uses this config
        if (!VersionCompat.isSingleplayerOwner(player) && NetworkCompat.canSend(player, OMTPackets.CONFIG_SYNC)) {
            NetworkCompat.sendToPlayer(player, OMTPackets.CONFIG_SYNC, new ConfigSyncPacket(OMTConfig.localJson()));
        }
    }
}

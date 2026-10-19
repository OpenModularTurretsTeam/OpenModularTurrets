package omtteam.openmodularturrets.client.network;

import omtteam.openmodularturrets.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.network.OMTPackets;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {
    }

    public static void init() {
        ClientNetworkCompat.registerHandler(OMTPackets.CONFIG_SYNC, packet -> OMTConfig.applyServerConfig(packet.json()));
    }
}

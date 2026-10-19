package omtteam.openmodularturrets.client.compat;

import omtteam.openmodularturrets.client.platform.ClientPlatform;
import omtteam.openmodularturrets.compat.network.PacketType;

import java.util.function.Consumer;

/**
 * Client-side counterpart of {@link omtteam.openmodularturrets.compat.network.NetworkCompat}; the transport is
 * {@link omtteam.openmodularturrets.client.platform.ClientNetwork}.
 */
public final class ClientNetworkCompat {
    private ClientNetworkCompat() {
    }

    /**
     * Registers the client handler of a server-to-client packet. The handler runs on the client thread.
     */
    public static <T> void registerHandler(PacketType<T> type, Consumer<T> handler) {
        ClientPlatform.network().registerHandler(type, handler);
    }

    public static <T> void sendToServer(PacketType<T> type, T packet) {
        ClientPlatform.network().sendToServer(type, packet);
    }
}

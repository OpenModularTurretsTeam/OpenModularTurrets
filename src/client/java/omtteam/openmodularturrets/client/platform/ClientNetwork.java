package omtteam.openmodularturrets.client.platform;

import omtteam.openmodularturrets.compat.network.PacketType;

import java.util.function.Consumer;

/**
 * Client side of the packet transport of the running loader. Client code uses {@code ClientNetworkCompat}, which
 * forwards here.
 */
public interface ClientNetwork {
    /**
     * Registers the handler of a server-to-client packet; it runs on the client thread.
     */
    <T> void registerHandler(PacketType<T> type, Consumer<T> handler);

    <T> void sendToServer(PacketType<T> type, T packet);
}

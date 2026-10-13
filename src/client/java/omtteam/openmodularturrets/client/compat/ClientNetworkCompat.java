package omtteam.openmodularturrets.client.compat;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import omtteam.openmodularturrets.compat.network.PacketType;

import java.util.function.Consumer;

/**
 * Client-side counterpart of {@link omtteam.openmodularturrets.compat.network.NetworkCompat}.
 */
public final class ClientNetworkCompat {
    private ClientNetworkCompat() {
    }

    /**
     * Registers the client handler of a server-to-client packet. The handler runs on the client thread.
     */
    public static <T> void registerHandler(PacketType<T> type, Consumer<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type.payloadType(), (payload, context) -> handler.accept(payload.packet()));
    }

    public static <T> void sendToServer(PacketType<T> type, T packet) {
        ClientPlayNetworking.send(type.wrap(packet));
    }
}

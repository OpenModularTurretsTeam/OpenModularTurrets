package omtteam.openmodularturrets.client.compat;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import omtteam.openmodularturrets.compat.network.PacketType;
//? if <1.21 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
*///?}

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
        //? if >=1.21 {
        ClientPlayNetworking.registerGlobalReceiver(type.payloadType(), (payload, context) -> handler.accept(payload.packet()));
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(type.id(), (client, listener, buf, sender) -> {
            T packet = type.decode(buf); // decode before the buffer is released
            client.execute(() -> handler.accept(packet));
        });
        *///?}
    }

    public static <T> void sendToServer(PacketType<T> type, T packet) {
        //? if >=1.21 {
        ClientPlayNetworking.send(type.wrap(packet));
        //?} else {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        type.encode(buf, packet);
        ClientPlayNetworking.send(type.id(), buf);
        *///?}
    }
}

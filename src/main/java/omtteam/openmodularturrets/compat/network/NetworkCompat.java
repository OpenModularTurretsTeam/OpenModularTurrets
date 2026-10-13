package omtteam.openmodularturrets.compat.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Version-independent packet registration and server-side sending.
 * Client-side counterparts live in {@code omtteam.openmodularturrets.client.compat.ClientNetworkCompat}.
 */
public final class NetworkCompat {
    private NetworkCompat() {
    }

    /**
     * Handles a client-to-server packet. Always runs on the server thread.
     * Implementations must re-validate everything (permissions, distance, …) — never trust the client.
     */
    @FunctionalInterface
    public interface ServerHandler<T> {
        void handle(T packet, ServerPlayer player);
    }

    /**
     * Registers a client-to-server packet and its server-side handler. Call from common init.
     */
    public static <T> PacketType<T> registerC2S(Identifier id, BiConsumer<FriendlyByteBuf, T> encoder,
                                                Function<FriendlyByteBuf, T> decoder, ServerHandler<T> handler) {
        PacketType<T> type = new PacketType<>(id, encoder, decoder);
        PayloadTypeRegistry.serverboundPlay().register(type.payloadType(), type.codec());
        // Fabric runs payload handlers on the server thread on 1.20.5+
        ServerPlayNetworking.registerGlobalReceiver(type.payloadType(),
                (payload, context) -> handler.handle(payload.packet(), context.player()));
        return type;
    }

    /**
     * Registers a server-to-client packet. Call from common init; register the client handler with
     * {@code ClientNetworkCompat#registerHandler} from client init.
     */
    public static <T> PacketType<T> registerS2C(Identifier id, BiConsumer<FriendlyByteBuf, T> encoder,
                                                Function<FriendlyByteBuf, T> decoder) {
        PacketType<T> type = new PacketType<>(id, encoder, decoder);
        PayloadTypeRegistry.clientboundPlay().register(type.payloadType(), type.codec());
        return type;
    }

    /**
     * Whether the player's client can receive packets of this type (i.e. has the mod installed).
     */
    public static boolean canSend(ServerPlayer player, PacketType<?> type) {
        return ServerPlayNetworking.canSend(player, type.payloadType());
    }

    public static <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        ServerPlayNetworking.send(player, type.wrap(packet));
    }
}

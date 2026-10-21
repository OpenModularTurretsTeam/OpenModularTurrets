package omtteam.openmodularturrets.compat.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.platform.Platform;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Version- and loader-independent packet registration and server-side sending; the transport is
 * {@link omtteam.openmodularturrets.platform.Network}.
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
    public static <T> PacketType<T> registerC2S(ResourceLocation id, BiConsumer<FriendlyByteBuf, T> encoder,
                                                Function<FriendlyByteBuf, T> decoder, ServerHandler<T> handler) {
        PacketType<T> type = new PacketType<>(id, encoder, decoder);
        Platform.network().registerC2S(type, handler);
        return type;
    }

    /**
     * Registers a server-to-client packet. Call from common init; register the client handler with
     * {@code ClientNetworkCompat#registerHandler} from client init.
     */
    public static <T> PacketType<T> registerS2C(ResourceLocation id, BiConsumer<FriendlyByteBuf, T> encoder,
                                                Function<FriendlyByteBuf, T> decoder) {
        PacketType<T> type = new PacketType<>(id, encoder, decoder);
        Platform.network().registerS2C(type);
        return type;
    }

    /**
     * Whether the player's client can receive packets of this type (i.e. has the mod installed).
     */
    public static boolean canSend(ServerPlayer player, PacketType<?> type) {
        return Platform.network().canSend(player, type);
    }

    public static <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        Platform.network().sendToPlayer(player, type, packet);
    }
}

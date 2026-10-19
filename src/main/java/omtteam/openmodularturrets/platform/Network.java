package omtteam.openmodularturrets.platform;

import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.compat.network.NetworkCompat;
import omtteam.openmodularturrets.compat.network.PacketType;

/**
 * Packet transport of the running loader. Gameplay code uses {@link NetworkCompat}, which forwards here.
 */
public interface Network {
    /**
     * Registers a client-to-server packet; the handler runs on the server thread.
     * On NeoForge this must happen before {@code RegisterPayloadHandlersEvent}, otherwise it throws.
     */
    <T> void registerC2S(PacketType<T> type, NetworkCompat.ServerHandler<T> handler);

    /**
     * Registers a server-to-client packet; its handler comes from {@code ClientNetwork#registerHandler}.
     * On NeoForge this must happen before {@code RegisterPayloadHandlersEvent}, otherwise it throws.
     */
    <T> void registerS2C(PacketType<T> type);

    /**
     * Whether the player's client can receive this packet type (has the mod installed).
     */
    boolean canSend(ServerPlayer player, PacketType<?> type);

    <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet);
}

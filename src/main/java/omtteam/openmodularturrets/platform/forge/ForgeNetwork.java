package omtteam.openmodularturrets.platform.forge;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import omtteam.openmodularturrets.compat.network.NetworkCompat;
import omtteam.openmodularturrets.compat.network.PacketType;
import omtteam.openmodularturrets.platform.Network;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * One Forge event channel per packet type, named by its id (like the raw channels on Fabric 1.20.1). Channels accept a
 * missing remote, so, like on Fabric, a client or server without the mod can still connect. Packets are decoded on the
 * network thread and handled on the main thread. Server-to-client packets are dispatched to the handler the client
 * registers later ({@code ForgeClientNetwork}), so main never touches client code.
 */
public final class ForgeNetwork implements Network {
    private static final Logger LOGGER = LoggerFactory.getLogger("openmodularturrets");
    /** Version of this mod's channels; raise it when a packet's format changes incompatibly. */
    private static final String PROTOCOL_VERSION = "1";

    private final Map<PacketType<?>, EventNetworkChannel> channels = new ConcurrentHashMap<>();
    private final Map<PacketType<?>, Consumer<?>> clientHandlers = new ConcurrentHashMap<>();

    @Override
    public <T> void registerC2S(PacketType<T> type, NetworkCompat.ServerHandler<T> handler) {
        channel(type).addListener((NetworkEvent.ClientCustomPayloadEvent event) -> {
            NetworkEvent.Context context = event.getSource().get();
            ServerPlayer player = context.getSender();
            if (player != null) {
                T packet = type.decode(event.getPayload()); // decode before the buffer is released
                context.enqueueWork(() -> handler.handle(packet, player));
            }
            context.setPacketHandled(true);
        });
    }

    @Override
    public <T> void registerS2C(PacketType<T> type) {
        channel(type).addListener((NetworkEvent.ServerCustomPayloadEvent event) -> {
            NetworkEvent.Context context = event.getSource().get();
            T packet = type.decode(event.getPayload());
            context.enqueueWork(() -> dispatchToClient(type, packet));
            context.setPacketHandled(true);
        });
    }

    @Override
    public boolean canSend(ServerPlayer player, PacketType<?> type) {
        EventNetworkChannel channel = channels.get(type);
        return channel != null && channel.isRemotePresent(player.connection.connection);
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        player.connection.send(packet(NetworkDirection.PLAY_TO_CLIENT, type, packet));
    }

    /**
     * Sets the client handler of a server-to-client packet; called by {@code ForgeClientNetwork}.
     */
    public <T> void setClientHandler(PacketType<T> type, Consumer<T> handler) {
        clientHandlers.put(type, handler);
    }

    /** The vanilla packet carrying {@code packet} on its type's channel in {@code direction}. */
    public static <T> Packet<?> packet(NetworkDirection direction, PacketType<T> type, T packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        type.encode(buf, packet);
        return direction.buildPacket(Pair.of(buf, 0), type.id()).getThis();
    }

    private EventNetworkChannel channel(PacketType<?> type) {
        EventNetworkChannel channel = NetworkRegistry.ChannelBuilder.named(type.id())
                .networkProtocolVersion(() -> PROTOCOL_VERSION)
                .clientAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION))
                .serverAcceptedVersions(NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION))
                .eventNetworkChannel();
        channels.put(type, channel);
        return channel;
    }

    @SuppressWarnings("unchecked")
    private <T> void dispatchToClient(PacketType<T> type, T packet) {
        Consumer<T> handler = (Consumer<T>) clientHandlers.get(type);
        if (handler == null) {
            LOGGER.warn("No client handler for packet {}, dropped", type.id());
            return;
        }
        handler.accept(packet);
    }
}

package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import omtteam.openmodularturrets.compat.network.NetworkCompat;
import omtteam.openmodularturrets.compat.network.PacketType;
import omtteam.openmodularturrets.platform.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * NeoForge only accepts payloads during {@link RegisterPayloadHandlersEvent}, so registrations are recorded and
 * applied when it fires. Server-to-client payloads need their handler at that point too: they get a dispatcher that
 * looks up the handler the client registers later ({@code NeoForgeClientNetwork}), so main never touches client code.
 */
public final class NeoForgeNetwork implements Network {
    private static final Logger LOGGER = LoggerFactory.getLogger("openmodularturrets");
    /** Version of this mod's channel set; raise it when a packet's format changes incompatibly. */
    private static final String PROTOCOL_VERSION = "1";

    private final List<Consumer<PayloadRegistrar>> pending = new ArrayList<>();
    private final Map<PacketType<?>, Consumer<?>> clientHandlers = new ConcurrentHashMap<>();
    private boolean registered;

    public NeoForgeNetwork(IEventBus modBus) {
        modBus.addListener(this::onRegisterPayloads);
    }

    @Override
    public <T> void registerC2S(PacketType<T> type, NetworkCompat.ServerHandler<T> handler) {
        record(registrar -> registrar.playToServer(type.payloadType(), type.codec(),
                (payload, context) -> handler.handle(payload.packet(), (ServerPlayer) context.player())));
    }

    @Override
    public <T> void registerS2C(PacketType<T> type) {
        record(registrar -> registrar.playToClient(type.payloadType(), type.codec(),
                (payload, context) -> dispatchToClient(type, payload.packet())));
    }

    @Override
    public boolean canSend(ServerPlayer player, PacketType<?> type) {
        return player.connection.hasChannel(type.payloadType());
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        PacketDistributor.sendToPlayer(player, type.wrap(packet));
    }

    /**
     * Sets the client handler of a server-to-client packet; called by {@code NeoForgeClientNetwork}.
     */
    public <T> void setClientHandler(PacketType<T> type, Consumer<T> handler) {
        clientHandlers.put(type, handler);
    }

    private void record(Consumer<PayloadRegistrar> registration) {
        if (registered) {
            throw new IllegalStateException("Packets must be registered before RegisterPayloadHandlersEvent");
        }
        pending.add(registration);
    }

    private void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        // optional: like on Fabric, a missing channel on the other side is no reason to refuse the connection
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION).executesOn(HandlerThread.MAIN).optional();
        pending.forEach(registration -> registration.accept(registrar));
        pending.clear();
        registered = true;
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

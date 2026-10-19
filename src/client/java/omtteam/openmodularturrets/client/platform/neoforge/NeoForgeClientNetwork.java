package omtteam.openmodularturrets.client.platform.neoforge;

import net.neoforged.neoforge.network.PacketDistributor;
import omtteam.openmodularturrets.client.platform.ClientNetwork;
import omtteam.openmodularturrets.compat.network.PacketType;
import omtteam.openmodularturrets.platform.neoforge.NeoForgeNetwork;

import java.util.function.Consumer;

/**
 * Client handlers are passed to {@link NeoForgeNetwork}, which registered the payloads with a dispatcher.
 */
public final class NeoForgeClientNetwork implements ClientNetwork {
    private final NeoForgeNetwork network;

    public NeoForgeClientNetwork(NeoForgeNetwork network) {
        this.network = network;
    }

    @Override
    public <T> void registerHandler(PacketType<T> type, Consumer<T> handler) {
        network.setClientHandler(type, handler);
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        PacketDistributor.sendToServer(type.wrap(packet));
    }
}

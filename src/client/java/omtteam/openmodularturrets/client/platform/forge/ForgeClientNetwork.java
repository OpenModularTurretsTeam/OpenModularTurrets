package omtteam.openmodularturrets.client.platform.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.network.NetworkDirection;
import omtteam.openmodularturrets.client.platform.ClientNetwork;
import omtteam.openmodularturrets.compat.network.PacketType;
import omtteam.openmodularturrets.platform.forge.ForgeNetwork;

import java.util.function.Consumer;

/**
 * Client handlers are passed to {@link ForgeNetwork}, whose channels dispatch server-to-client packets to them.
 */
public final class ForgeClientNetwork implements ClientNetwork {
    private final ForgeNetwork network;

    public ForgeClientNetwork(ForgeNetwork network) {
        this.network = network;
    }

    @Override
    public <T> void registerHandler(PacketType<T> type, Consumer<T> handler) {
        network.setClientHandler(type, handler);
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection != null) {
            connection.send(ForgeNetwork.packet(NetworkDirection.PLAY_TO_SERVER, type, packet));
        }
    }
}

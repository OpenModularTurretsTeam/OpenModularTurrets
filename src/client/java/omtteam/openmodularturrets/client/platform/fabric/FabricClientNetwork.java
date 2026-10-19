package omtteam.openmodularturrets.client.platform.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import omtteam.openmodularturrets.client.platform.ClientNetwork;
import omtteam.openmodularturrets.compat.network.PacketType;
//? if <1.21 {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
*///?}

import java.util.function.Consumer;

public final class FabricClientNetwork implements ClientNetwork {
    @Override
    public <T> void registerHandler(PacketType<T> type, Consumer<T> handler) {
        //? if >=1.21 {
        ClientPlayNetworking.registerGlobalReceiver(type.payloadType(), (payload, context) -> handler.accept(payload.packet()));
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(type.id(), (client, listener, buf, sender) -> {
            T packet = type.decode(buf); // decode before the buffer is released
            client.execute(() -> handler.accept(packet));
        });
        *///?}
    }

    @Override
    public <T> void sendToServer(PacketType<T> type, T packet) {
        //? if >=1.21 {
        ClientPlayNetworking.send(type.wrap(packet));
        //?} else {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        type.encode(buf, packet);
        ClientPlayNetworking.send(type.id(), buf);
        *///?}
    }
}

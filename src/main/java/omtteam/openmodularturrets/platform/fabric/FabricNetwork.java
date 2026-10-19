package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.compat.network.NetworkCompat;
import omtteam.openmodularturrets.compat.network.PacketType;
import omtteam.openmodularturrets.platform.Network;
//? if >=1.21 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?} else {
/*import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
*///?}

public final class FabricNetwork implements Network {
    @Override
    public <T> void registerC2S(PacketType<T> type, NetworkCompat.ServerHandler<T> handler) {
        //? if >=1.21 {
        PayloadTypeRegistry.playC2S().register(type.payloadType(), type.codec());
        // Fabric runs payload handlers on the server thread on 1.20.5+
        ServerPlayNetworking.registerGlobalReceiver(type.payloadType(),
                (payload, context) -> handler.handle(payload.packet(), context.player()));
        //?} else {
        /*ServerPlayNetworking.registerGlobalReceiver(type.id(), (server, player, listener, buf, sender) -> {
            T packet = type.decode(buf); // decode before the buffer is released
            server.execute(() -> handler.handle(packet, player));
        });
        *///?}
    }

    @Override
    public <T> void registerS2C(PacketType<T> type) {
        //? if >=1.21 {
        PayloadTypeRegistry.playS2C().register(type.payloadType(), type.codec());
        //?}
    }

    @Override
    public boolean canSend(ServerPlayer player, PacketType<?> type) {
        //? if >=1.21 {
        return ServerPlayNetworking.canSend(player, type.payloadType());
        //?} else {
        /*return ServerPlayNetworking.canSend(player, type.id());
        *///?}
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, PacketType<T> type, T packet) {
        //? if >=1.21 {
        ServerPlayNetworking.send(player, type.wrap(packet));
        //?} else {
        /*FriendlyByteBuf buf = PacketByteBufs.create();
        type.encode(buf, packet);
        ServerPlayNetworking.send(player, type.id(), buf);
        *///?}
    }
}

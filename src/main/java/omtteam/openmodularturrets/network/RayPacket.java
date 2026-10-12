package omtteam.openmodularturrets.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.compat.network.NetworkCompat;

/**
 * S2C: draw a ray for a few ticks (OMLib {@code MessageRenderRay}; the arc turret's lightning is drawn jagged).
 *
 * @param argb     colour with alpha
 * @param duration ticks the ray stays visible
 */
public record RayPacket(Vec3 from, Vec3 to, int argb, int duration, boolean lightning) {
    /** oldmod sent rays to players within 120 blocks of the start. */
    private static final double RANGE = 120;

    public static void write(FriendlyByteBuf buf, RayPacket packet) {
        writeVec(buf, packet.from);
        writeVec(buf, packet.to);
        buf.writeInt(packet.argb);
        buf.writeVarInt(packet.duration);
        buf.writeBoolean(packet.lightning);
    }

    public static RayPacket read(FriendlyByteBuf buf) {
        return new RayPacket(readVec(buf), readVec(buf), buf.readInt(), buf.readVarInt(), buf.readBoolean());
    }

    private static void writeVec(FriendlyByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    private static Vec3 readVec(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    /** Sends the ray to every player near its start. */
    public static void send(ServerLevel level, RayPacket packet) {
        for (ServerPlayer player : level.players()) {
            if (player.position().distanceToSqr(packet.from) < RANGE * RANGE) {
                NetworkCompat.sendToPlayer(player, OMTPackets.RAY, packet);
            }
        }
    }
}

package omtteam.openmodularturrets.network;

import net.minecraft.network.FriendlyByteBuf;

/**
 * S2C: the server's config as JSON, sent on join.
 */
public record ConfigSyncPacket(String json) {
    private static final int MAX_LENGTH = 262144;

    public static void write(FriendlyByteBuf buf, ConfigSyncPacket packet) {
        buf.writeUtf(packet.json, MAX_LENGTH);
    }

    public static ConfigSyncPacket read(FriendlyByteBuf buf) {
        return new ConfigSyncPacket(buf.readUtf(MAX_LENGTH));
    }
}

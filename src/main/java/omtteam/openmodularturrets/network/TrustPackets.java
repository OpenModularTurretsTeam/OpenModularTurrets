package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

/**
 * Packets of the trusted player screen. Ported from OMLib {@code MessageAddTrustedPlayer},
 * {@code MessageRemoveTrustedPlayer} and {@code MessageModifyPermissions}; players are identified by UUID instead of
 * name, except when adding.
 */
public final class TrustPackets {
    /** Minecraft usernames have at most 16 characters. */
    public static final int MAX_NAME_LENGTH = 16;

    private TrustPackets() {
    }

    /** C2S: add a player (by name, resolved on the server). */
    public record Add(BlockPos pos, String name) {
        public static void write(FriendlyByteBuf buf, Add packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeUtf(packet.name, MAX_NAME_LENGTH);
        }

        public static Add read(FriendlyByteBuf buf) {
            return new Add(buf.readBlockPos(), buf.readUtf(MAX_NAME_LENGTH));
        }
    }

    /** C2S: remove a trusted player. */
    public record Remove(BlockPos pos, UUID player) {
        public static void write(FriendlyByteBuf buf, Remove packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeUUID(packet.player);
        }

        public static Remove read(FriendlyByteBuf buf) {
            return new Remove(buf.readBlockPos(), buf.readUUID());
        }
    }

    /** C2S: raise ({@code +1}) or lower ({@code -1}) the access level of a trusted player. */
    public record ChangeAccess(BlockPos pos, UUID player, int delta) {
        public static void write(FriendlyByteBuf buf, ChangeAccess packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeUUID(packet.player);
            buf.writeByte(packet.delta);
        }

        public static ChangeAccess read(FriendlyByteBuf buf) {
            return new ChangeAccess(buf.readBlockPos(), buf.readUUID(), buf.readByte());
        }
    }
}

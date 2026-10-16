package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * C2S packets of the camouflage menu.
 */
public final class CamoPackets {
    private CamoPackets() {
    }

    /** Sets light value and light opacity of a camouflaged block (1.12.2 {@code MessageAdjustLightValue/Opacity}). */
    public record SetLight(BlockPos pos, int value, int opacity) {
        public static void write(FriendlyByteBuf buf, SetLight packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeVarInt(packet.value);
            buf.writeVarInt(packet.opacity);
        }

        public static SetLight read(FriendlyByteBuf buf) {
            return new SetLight(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt());
        }
    }
}

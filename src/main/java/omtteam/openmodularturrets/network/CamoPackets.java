package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * C2S packets of the camouflage menu.
 */
public final class CamoPackets {
    private CamoPackets() {
    }

    /** Opens the camouflage menu of a base or trap (from the configure and trusted player screens). */
    public record OpenMenu(BlockPos pos) {
        public static void write(FriendlyByteBuf buf, OpenMenu packet) {
            buf.writeBlockPos(packet.pos);
        }

        public static OpenMenu read(FriendlyByteBuf buf) {
            return new OpenMenu(buf.readBlockPos());
        }
    }

    /** "Back" from the camouflage menu to the menu it was opened from ({@code CamoHost#openParentMenu}). */
    public record Back(BlockPos pos) {
        public static void write(FriendlyByteBuf buf, Back packet) {
            buf.writeBlockPos(packet.pos);
        }

        public static Back read(FriendlyByteBuf buf) {
            return new Back(buf.readBlockPos());
        }
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

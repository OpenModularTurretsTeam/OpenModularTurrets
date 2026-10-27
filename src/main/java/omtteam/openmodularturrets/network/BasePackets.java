package omtteam.openmodularturrets.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * C2S packets of the base and configure screens. Ported from 1.12.2 {@code MessageAdjustRange},
 * {@code MessageToggleMode}, {@code MessageSetBaseTargetingType}, {@code MessageToggleAttack*},
 * {@code MessageDropBase} and {@code MessageDropTurrets}.
 */
public final class BasePackets {
    private BasePackets() {
    }

    /** Actions that only need the position. */
    public enum Action {
        /** Admin: cycle the redstone mode. */
        TOGGLE_MODE,
        /** Change settings: toggle single/multi targeting. */
        TOGGLE_MULTI_TARGETING,
        /** Admin: destroy the base (drops it). */
        DROP_BASE,
        /** Admin: destroy all attached turrets (drops them). */
        DROP_TURRETS,
        /** Open GUI: (re)open the base menu, e.g. from the configure screen's "Back" button. */
        OPEN_MENU,
        /** Open GUI: sort the ammo slots ({@code TurretBaseInventory.sortAmmo}). */
        SORT_AMMO
    }

    public record SimpleAction(BlockPos pos, Action action) {
        public static void write(FriendlyByteBuf buf, SimpleAction packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeEnum(packet.action);
        }

        public static SimpleAction read(FriendlyByteBuf buf) {
            return new SimpleAction(buf.readBlockPos(), buf.readEnum(Action.class));
        }
    }

    /** Change settings: set the targeting range (as in 1.12.2, the new absolute range is sent). */
    public record SetRange(BlockPos pos, int range) {
        public static void write(FriendlyByteBuf buf, SetRange packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeVarInt(packet.range);
        }

        public static SetRange read(FriendlyByteBuf buf) {
            return new SetRange(buf.readBlockPos(), buf.readVarInt());
        }
    }

    public enum TargetCategory {
        MOBS, NEUTRALS, PLAYERS
    }

    /** Change settings: enable or disable a target category. */
    public record SetTargeting(BlockPos pos, TargetCategory category, boolean enabled) {
        public static void write(FriendlyByteBuf buf, SetTargeting packet) {
            buf.writeBlockPos(packet.pos);
            buf.writeEnum(packet.category);
            buf.writeBoolean(packet.enabled);
        }

        public static SetTargeting read(FriendlyByteBuf buf) {
            return new SetTargeting(buf.readBlockPos(), buf.readEnum(TargetCategory.class), buf.readBoolean());
        }
    }
}

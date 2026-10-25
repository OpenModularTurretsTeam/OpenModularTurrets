package omtteam.openmodularturrets.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * C2S messages of the base and configure GUIs, as on 1.20.1+. They replace the 3.x {@code MessageAdjustRange},
 * {@code MessageToggleMode}, {@code MessageSetBaseTargetingType}, {@code MessageToggleAttack*},
 * {@code MessageDropBase} and {@code MessageDropTurrets}; {@link BasePacketHandler} handles them.
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
        DROP_TURRETS
    }

    public enum TargetCategory {
        MOBS, NEUTRALS, PLAYERS
    }

    public static class SimpleAction implements IMessage {
        BlockPos pos;
        Action action;

        public SimpleAction() {
        }

        public SimpleAction(BlockPos pos, Action action) {
            this.pos = pos;
            this.action = action;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = BlockPos.fromLong(buf.readLong());
            action = readEnum(buf, Action.values());
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeLong(pos.toLong());
            buf.writeByte(action.ordinal());
        }
    }

    /** Admin: set the targeting range (the new absolute range is sent, as in 3.x). */
    public static class SetRange implements IMessage {
        BlockPos pos;
        int range;

        public SetRange() {
        }

        public SetRange(BlockPos pos, int range) {
            this.pos = pos;
            this.range = range;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = BlockPos.fromLong(buf.readLong());
            range = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeLong(pos.toLong());
            buf.writeInt(range);
        }
    }

    /** Change settings: enable or disable a target category. */
    public static class SetTargeting implements IMessage {
        BlockPos pos;
        TargetCategory category;
        boolean enabled;

        public SetTargeting() {
        }

        public SetTargeting(BlockPos pos, TargetCategory category, boolean enabled) {
            this.pos = pos;
            this.category = category;
            this.enabled = enabled;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            pos = BlockPos.fromLong(buf.readLong());
            category = readEnum(buf, TargetCategory.values());
            enabled = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeLong(pos.toLong());
            buf.writeByte(category.ordinal());
            buf.writeBoolean(enabled);
        }
    }

    /** An enum constant by ordinal; an unknown ordinal (a broken or forged message) fails the decoding. */
    private static <E extends Enum<E>> E readEnum(ByteBuf buf, E[] values) {
        int ordinal = buf.readUnsignedByte();
        if (ordinal >= values.length) {
            throw new IllegalArgumentException("Unknown " + values[0].getDeclaringClass().getSimpleName() + " " + ordinal);
        }
        return values[ordinal];
    }
}

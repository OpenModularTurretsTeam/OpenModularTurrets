package openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.tileentity.turrets.TurretHead;

public class MessageDropTurrets implements IMessage {
    private int x, y, z;

    public MessageDropTurrets() {
    }

    public static class MessageHandlerDropTurrets implements IMessageHandler<MessageDropTurrets, IMessage> {
        @Override
        public IMessage onMessage(MessageDropTurrets message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;

            if (world.getTileEntity(new BlockPos(message.getX() + 1, message.getY(), message.getZ())) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX() + 1, message.getY(), message.getZ()), true);
            }

            if (world.getTileEntity(new BlockPos(message.getX() - 1, message.getY(), message.getZ())) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX() - 1, message.getY(), message.getZ()), true);
            }

            if (world.getTileEntity(new BlockPos(message.getX(), message.getY() + 1, message.getZ())) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX(), message.getY() + 1, message.getZ()), true);
            }

            if (world.getTileEntity(new BlockPos(message.getX(), message.getY() - 1, message.getZ())) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX(), message.getY() - 1, message.getZ()), true);
            }

            if (world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ() + 1)) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX(), message.getY(), message.getZ() + 1), true);
            }

            if (world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ() - 1)) instanceof TurretHead) {
                world.destroyBlock(new BlockPos(message.getX(), message.getY(), message.getZ() - 1), true);
            }

            return null;
        }
    }

    public MessageDropTurrets(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }
}

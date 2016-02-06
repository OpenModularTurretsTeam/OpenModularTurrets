package openmodularturrets.network.messages;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turrets.TurretHead;

public class MessageDropTurrets implements IMessage {
    private int x, y, z;

    public MessageDropTurrets() {
    }

    public static class MessageHandlerDropTurrets implements IMessageHandler<MessageDropTurrets, IMessage> {
        @Override
        public IMessage onMessage(MessageDropTurrets message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;

            if (world.getTileEntity(message.getX() + 1, message.getY(), message.getZ()) instanceof TurretHead) {
                world.func_147480_a(message.getX() + 1, message.getY(), message.getZ(), true);
            }

            if (world.getTileEntity(message.getX() - 1, message.getY(), message.getZ()) instanceof TurretHead) {
                world.func_147480_a(message.getX() - 1, message.getY(), message.getZ(), true);
            }

            if (world.getTileEntity(message.getX(), message.getY() + 1, message.getZ()) instanceof TurretHead) {
                world.func_147480_a(message.getX(), message.getY() + 1, message.getZ(), true);
            }

            if (world.getTileEntity(message.getX(), message.getY() - 1, message.getZ()) instanceof TurretHead) {
                world.func_147480_a(message.getX(), message.getY() - 1, message.getZ(), true);
            }

            if (world.getTileEntity(message.getX(), message.getY(), message.getZ() + 1) instanceof TurretHead) {
                world.func_147480_a(message.getX(), message.getY(), message.getZ() + 1, true);
            }

            if (world.getTileEntity(message.getX(), message.getY(), message.getZ() - 1) instanceof TurretHead) {
                world.func_147480_a(message.getX(), message.getY(), message.getZ() - 1, true);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}

package openmodularturrets.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turrets.TurretHead;
import openmodularturrets.util.PlayerUtil;

public class MessageDropTurrets implements IMessage {

    private int x, y, z;

    public MessageDropTurrets() {}

    public static class MessageHandlerDropTurrets implements IMessageHandler<MessageDropTurrets, IMessage> {

        @Override
        public IMessage onMessage(MessageDropTurrets message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity entity = world.getTileEntity(message.getX(), message.getY(), message.getZ());
            TurretBase machine = null;
            if (entity instanceof TurretBase) {
                machine = (TurretBase) entity;
            }
            if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {

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

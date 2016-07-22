package openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class MessageRemoveTrustedPlayer implements IMessage {
    private int x, y, z;
    private String player;

    public MessageRemoveTrustedPlayer() {
    }

    public static class MessageHandlerRemoveTrustedPlayer implements IMessageHandler<MessageRemoveTrustedPlayer, IMessage> {
        @Override
        public IMessage onMessage(MessageRemoveTrustedPlayer messageIn, MessageContext ctxIn) {
            final MessageRemoveTrustedPlayer message = messageIn;
            final MessageContext ctx = ctxIn;
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    World world = ctx.getServerHandler().playerEntity.worldObj;
                    TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));

                    turret.removeTrustedPlayer(message.getPlayer());
                }
            });
            return null;
        }
    }

    public MessageRemoveTrustedPlayer(int x, int y, int z, String player) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.player = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        ByteBufUtils.writeUTF8String(buf, this.player);
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

    private String getPlayer() {
        return player;
    }
}

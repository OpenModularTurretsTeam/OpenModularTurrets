package openmodularturrets.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

public class MessageRemoveTrustedPlayer implements IMessage {

    private int x, y, z;
    private String player;

    public MessageRemoveTrustedPlayer() {}

    public static class MessageHandlerRemoveTrustedPlayer
            implements IMessageHandler<MessageRemoveTrustedPlayer, IMessage> {

        @Override
        public IMessage onMessage(MessageRemoveTrustedPlayer message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity entity = world.getTileEntity(message.getX(), message.getY(), message.getZ());
            TurretBase turret = null;
            if (entity instanceof TurretBase) {
                turret = (TurretBase) entity;
            }
            if (turret != null && PlayerUtil.isPlayerAdmin(player, turret)) {
                turret.removeTrustedPlayer(message.getPlayer());
            }
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

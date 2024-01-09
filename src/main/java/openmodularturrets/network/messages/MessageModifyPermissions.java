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

public class MessageModifyPermissions implements IMessage {

    private int x, y, z;
    private String player, perm;
    private boolean canDo;

    public MessageModifyPermissions() {}

    public static class MessageHandlerModifyPermissions implements IMessageHandler<MessageModifyPermissions, IMessage> {

        @Override
        public IMessage onMessage(MessageModifyPermissions message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity entity = world.getTileEntity(message.getX(), message.getY(), message.getZ());
            TurretBase turret = null;
            if (entity instanceof TurretBase) {
                turret = (TurretBase) entity;
            }
            if (turret != null && PlayerUtil.isPlayerAdmin(player, turret)) {
                if (message.getPerm().equals("gui")) {
                    turret.getTrustedPlayer(message.getPlayer()).setCanOpenGUI(message.canDo);
                }

                if (message.getPerm().equals("targeting")) {
                    turret.getTrustedPlayer(message.getPlayer()).setCanChangeTargeting(message.canDo);
                }

                if (message.getPerm().equals("isAdmin")) {
                    turret.getTrustedPlayer(message.getPlayer()).setAdmin(message.canDo);
                }
            }
            return null;
        }
    }

    public MessageModifyPermissions(int x, int y, int z, String player, String perm, boolean canDo) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
        this.perm = perm;
        this.canDo = canDo;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.player = ByteBufUtils.readUTF8String(buf);
        this.perm = ByteBufUtils.readUTF8String(buf);
        this.canDo = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.player);
        ByteBufUtils.writeUTF8String(buf, this.perm);
        buf.writeBoolean(canDo);
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

    private String getPerm() {
        return perm;
    }

    public boolean getCanDo() {
        return canDo;
    }
}

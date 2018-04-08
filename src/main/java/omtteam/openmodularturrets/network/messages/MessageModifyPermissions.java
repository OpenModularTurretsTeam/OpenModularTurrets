package omtteam.openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.omlib.tileentity.ITrustedPlayersManager;
import omtteam.omlib.util.PlayerUtil;

@SuppressWarnings("unused")
public class MessageModifyPermissions implements IMessage {
    private int x, y, z;
    private String player, perm;
    private boolean canDo;

    public MessageModifyPermissions() {
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerModifyPermissions implements IMessageHandler<MessageModifyPermissions, IMessage> {
        @Override
        public IMessage onMessage(MessageModifyPermissions messageIn, MessageContext ctxIn) {
            final MessageModifyPermissions message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().playerEntity.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                ITrustedPlayersManager machine = null;
                if (entity instanceof ITrustedPlayersManager) {
                    machine = (ITrustedPlayersManager) entity;
                }
                if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {

                    if (message.getPerm().equals("gui")) {
                        machine.getTrustedPlayer(message.getPlayer()).setCanOpenGUI(message.canDo);
                    }

                    if (message.getPerm().equals("targeting")) {
                        machine.getTrustedPlayer(message.getPlayer()).setCanChangeTargeting(message.canDo);
                    }

                    if (message.getPerm().equals("isAdmin")) {
                        machine.getTrustedPlayer(message.getPlayer()).setAdmin(message.canDo);
                    }
                }
            });
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

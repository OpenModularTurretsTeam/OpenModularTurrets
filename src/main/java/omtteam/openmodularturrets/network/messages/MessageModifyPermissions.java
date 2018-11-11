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
import omtteam.omlib.util.EnumAccessMode;
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.omlib.util.player.TrustedPlayer;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageModifyPermissions implements IMessage {
    private int x, y, z, change;
    private String player;

    public MessageModifyPermissions() {
    }

    public MessageModifyPermissions(int x, int y, int z, String player, int change) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.player = player;
        this.change = change;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.player = ByteBufUtils.readUTF8String(buf);
        this.change = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.player);
        buf.writeInt(this.change);
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

    private int getChange() {
        return change;
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerModifyPermissions implements IMessageHandler<MessageModifyPermissions, IMessage> {
        @Override
        public IMessage onMessage(MessageModifyPermissions messageIn, MessageContext ctxIn) {
            final MessageModifyPermissions message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().player.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().player;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                TurretBase machine = null;
                if (entity instanceof TurretBase) {
                    machine = (TurretBase) entity;
                }
                if (machine != null && PlayerUtil.isTrustedPlayerAdmin(player, machine)) {
                    TrustedPlayer trustedPlayer = machine.getTrustedPlayer(message.getPlayer());
                    if (trustedPlayer != null) {
                        int newMode = trustedPlayer.getAccessMode().ordinal() + message.getChange();
                        if (!(newMode > 3 || newMode < 0)) {
                            trustedPlayer.setAccessMode(EnumAccessMode.values()[newMode]);
                        }
                    }
                }
            });
            return null;
        }
    }
}

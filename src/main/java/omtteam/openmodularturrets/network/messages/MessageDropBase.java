package omtteam.openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.player.PlayerUtil;

@SuppressWarnings("unused")
public class MessageDropBase implements IMessage {
    private int x, y, z;
    private String player;

    public MessageDropBase() {
    }

    public MessageDropBase(int x, int y, int z) {
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

    public String getOwner() {
        return player;
    }

    public static class MessageHandlerDropBase implements IMessageHandler<MessageDropBase, IMessage> {
        @Override
        public IMessage onMessage(MessageDropBase messageIn, MessageContext ctxIn) {
            final MessageDropBase message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().player.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().player;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                TileEntityOwnedBlock machine = null;
                if (entity instanceof TileEntityOwnedBlock) {
                    machine = (TileEntityOwnedBlock) entity;
                }
                if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {
                    world.destroyBlock(new BlockPos(message.getX(), message.getY(), message.getZ()), true);
                }
            });
            return null;
        }
    }
}

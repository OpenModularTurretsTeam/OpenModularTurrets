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
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageAdjustRange implements IMessage {
    private int x, y, z;
    private int range;

    public MessageAdjustRange() {
    }

    public MessageAdjustRange(int x, int y, int z, int range) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.range = range;
    }

    public int getBaseRange() {
        return range;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.range = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.range);
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

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerAdjustYAxisDetect implements IMessageHandler<MessageAdjustRange, IMessage> {
        @Override
        public IMessage onMessage(MessageAdjustRange messageIn, MessageContext ctxIn) {
            final MessageAdjustRange message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().player.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().player;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                TurretBase machine = null;
                if (entity instanceof TurretBase) {
                    machine = (TurretBase) entity;
                }
                if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {
                    machine.updateMaxRange();
                    machine.getTargetingSettings().setRange(message.getBaseRange());
                    machine.informUpdate();
                }
            });
            return null;
        }
    }
}

package omtteam.openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageAdjustYAxisDetect implements IMessage {
    private int x, y, z;
    private int y_axis_detect;

    public MessageAdjustYAxisDetect() {
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerAdjustYAxisDetect implements IMessageHandler<MessageAdjustYAxisDetect, IMessage> {
        @Override
        public IMessage onMessage(MessageAdjustYAxisDetect messageIn, MessageContext ctxIn) {
            final MessageAdjustYAxisDetect message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().playerEntity.worldObj).addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    World world = ctx.getServerHandler().playerEntity.worldObj;
                    TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));

                    turret.setyAxisDetect(message.getYAxisDetect());
                }
            });
            return null;
        }
    }

    public MessageAdjustYAxisDetect(int x, int y, int z, int y_axis_detect) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.y_axis_detect = y_axis_detect;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.y_axis_detect = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeInt(this.y_axis_detect);
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

    private int getYAxisDetect() {
        return y_axis_detect;
    }
}

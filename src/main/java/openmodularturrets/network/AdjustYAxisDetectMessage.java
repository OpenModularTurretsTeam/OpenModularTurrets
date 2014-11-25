package openmodularturrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class AdjustYAxisDetectMessage implements IMessage, IMessageHandler<AdjustYAxisDetectMessage, IMessage> {
    private int x, y, z;
    private int y_axis_detect;

    public AdjustYAxisDetectMessage() { }

    public AdjustYAxisDetectMessage(int x, int y, int z, int y_axis_detect) {
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

    @Override
    public IMessage onMessage(AdjustYAxisDetectMessage message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turret = (TurretBase)world.getTileEntity(message.getX(), message.getY(), message.getZ());

        turret.setyAxisDetect(message.getYAxisDetect());

        return null;
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

    public int getYAxisDetect() {
        return y_axis_detect;
    }
}

package openmodularturrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class SetBaseTargetingType implements IMessage, IMessageHandler<SetBaseTargetingType, IMessage> {
    private int x, y, z;

    public SetBaseTargetingType() {
    }

    public SetBaseTargetingType(int x, int y, int z) {
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

    @Override
    public IMessage onMessage(SetBaseTargetingType message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turretbase = (TurretBase) world.getTileEntity(message.getX(), message.getY(), message.getZ());
        turretbase.multiTargeting = !turretbase.multiTargeting;
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
}

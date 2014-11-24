package openmodularturrets.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretBase.TurretBase;
import net.minecraft.world.World;

public class SetTurretOwnerMessage implements IMessage, IMessageHandler<SetTurretOwnerMessage, IMessage> {
    private int x, y, z;
    private String player;

    public SetTurretOwnerMessage() {
    }

    public SetTurretOwnerMessage(int x, int y, int z, String player) {
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

    @Override
    public IMessage onMessage(SetTurretOwnerMessage message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turret = (TurretBase)world.getTileEntity(message.getX(), message.getY(), message.getZ());

        turret.setOwner(message.getOwner());

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

    public String getOwner() {
        return player;
    }
}

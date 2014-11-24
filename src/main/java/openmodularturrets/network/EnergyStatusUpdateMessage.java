package openmodularturrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretBase.TurretBase;

public class EnergyStatusUpdateMessage implements IMessage, IMessageHandler<EnergyStatusUpdateMessage, IMessage> {
    private int x, y, z;
    private int energy;

    public EnergyStatusUpdateMessage() {
    }

    public EnergyStatusUpdateMessage(int x, int y, int z, int energy) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.energy = energy;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.energy = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeInt(this.energy);
    }

    @Override
    public IMessage onMessage(EnergyStatusUpdateMessage message, MessageContext ctx) {
        World world = Minecraft.getMinecraft().thePlayer.worldObj;
        TurretBase turret = (TurretBase)world.getTileEntity(message.getX(), message.getY(), message.getZ());

        if (turret != null) {
            turret.setEnergyStored(message.getEnergy());
        }

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

    public int getEnergy() {
        return energy;
    }
}

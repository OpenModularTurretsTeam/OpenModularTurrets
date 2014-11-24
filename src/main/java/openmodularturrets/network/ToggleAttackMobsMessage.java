package openmodularturrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretBase.TurretBase;

public class ToggleAttackMobsMessage implements IMessage, IMessageHandler<ToggleAttackMobsMessage, IMessage> {
    private int x, y, z;
    private boolean attack_mobs;

    public ToggleAttackMobsMessage() {
    }

    public ToggleAttackMobsMessage(int x, int y, int z, boolean attack_mobs) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.attack_mobs = attack_mobs;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.attack_mobs = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeBoolean(this.attack_mobs);
    }

    @Override
    public IMessage onMessage(ToggleAttackMobsMessage message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turret = (TurretBase)world.getTileEntity(message.getX(), message.getY(), message.getZ());

        turret.setAttacksMobs(message.doAttackMobs());

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

    public boolean doAttackMobs() {
        return attack_mobs;
    }
}

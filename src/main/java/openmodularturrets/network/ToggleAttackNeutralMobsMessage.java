package openmodularturrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class ToggleAttackNeutralMobsMessage implements IMessage, IMessageHandler<ToggleAttackNeutralMobsMessage, IMessage> {
    private int x, y, z;
    private boolean attack_neutrals;

    public ToggleAttackNeutralMobsMessage() {
    }

    public ToggleAttackNeutralMobsMessage(int x, int y, int z, boolean attack_neutrals) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.attack_neutrals = attack_neutrals;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.attack_neutrals = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeBoolean(this.attack_neutrals);
    }

    @Override
    public IMessage onMessage(ToggleAttackNeutralMobsMessage message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turret = (TurretBase) world.getTileEntity(message.getX(), message.getY(), message.getZ());

        turret.setAttacksNeutrals(message.doAttackNeutrals());
        return message;
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

    public boolean doAttackNeutrals() {
        return attack_neutrals;
    }
}

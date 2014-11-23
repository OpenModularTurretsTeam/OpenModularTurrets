package modularTurrets.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.world.World;

public class ToggleAttackPlayersMessage implements IMessage, IMessageHandler<ToggleAttackPlayersMessage, IMessage> {
    private int x, y, z;
    private boolean attack_players;

    public ToggleAttackPlayersMessage() {
    }

    public ToggleAttackPlayersMessage(int x, int y, int z, boolean attack_players) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.attack_players = attack_players;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.attack_players = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeBoolean(this.attack_players);
    }

    @Override
    public IMessage onMessage(ToggleAttackPlayersMessage message, MessageContext ctx) {
        World world = ctx.getServerHandler().playerEntity.worldObj;
        TurretBase turret = (TurretBase)world.getTileEntity(message.getX(), message.getY(), message.getZ());

        turret.setAttacksPlayers(message.doAttackPlayers());

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

    public boolean doAttackPlayers() {
        return attack_players;
    }
}

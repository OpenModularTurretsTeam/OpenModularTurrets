package openmodularturrets.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

public class MessageToggleAttackPlayers implements IMessage {

    private int x, y, z;
    private boolean attack_players;

    public MessageToggleAttackPlayers() {}

    public static class MessageHandlerToggleAttackPlayers
            implements IMessageHandler<MessageToggleAttackPlayers, IMessage> {

        @Override
        public IMessage onMessage(MessageToggleAttackPlayers message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.getEntityWorld();
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity entity = world.getTileEntity(message.getX(), message.getY(), message.getZ());
            TurretBase turret = null;
            if (entity instanceof TurretBase) {
                turret = (TurretBase) entity;
            }
            if (turret != null && PlayerUtil.isPlayerAdmin(player, turret)) {
                turret.setAttacksPlayers(message.doAttackPlayers());
            }
            return null;
        }
    }

    public MessageToggleAttackPlayers(int x, int y, int z, boolean attack_players) {
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

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }

    private boolean doAttackPlayers() {
        return attack_players;
    }
}

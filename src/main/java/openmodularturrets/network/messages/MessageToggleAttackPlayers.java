package openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class MessageToggleAttackPlayers implements IMessage {
    private int x, y, z;
    private boolean attack_players;

    public MessageToggleAttackPlayers() {
    }

    public static class MessageHandlerToggleAttackPlayers implements IMessageHandler<MessageToggleAttackPlayers, IMessage> {
        @Override
        public IMessage onMessage(MessageToggleAttackPlayers message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));

            turret.setAttacksPlayers(message.doAttackPlayers());
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

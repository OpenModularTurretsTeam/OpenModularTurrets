package omtteam.openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.omlib.util.PlayerUtil;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageToggleAttackPlayers implements IMessage {
    private int x, y, z;
    private boolean attack_players;

    public MessageToggleAttackPlayers() {
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerToggleAttackPlayers implements IMessageHandler<MessageToggleAttackPlayers, IMessage> {
        @Override
        public IMessage onMessage(MessageToggleAttackPlayers messageIn, MessageContext ctxIn) {
            final MessageToggleAttackPlayers message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().playerEntity.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                TurretBase machine = null;
                if (entity instanceof TurretBase) {
                    machine = (TurretBase) entity;
                }
                if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {
                    machine.setAttacksPlayers(message.doAttackPlayers());
                }
            });
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

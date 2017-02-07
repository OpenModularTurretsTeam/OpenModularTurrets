package omtteam.openmodularturrets.network.messages;


import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageAddTrustedPlayer implements IMessage {
    private int x, y, z;
    private String player;

    public MessageAddTrustedPlayer() {
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerAddTrustedPlayer implements IMessageHandler<MessageAddTrustedPlayer, IMessage> {
        @Override
        public IMessage onMessage(MessageAddTrustedPlayer messageIn, MessageContext ctxIn) {
            final MessageAddTrustedPlayer message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().playerEntity.getEntityWorld()).addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    World world = ctx.getServerHandler().playerEntity.getEntityWorld();
                    TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                    turret.addTrustedPlayer(message.getPlayer());
                }
            });
            return null;
        }
    }

    public MessageAddTrustedPlayer(int x, int y, int z, String player) {
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

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }

    private String getPlayer() {
        return player;
    }
}

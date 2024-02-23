package openmodularturrets.network.messages;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

public class MessageDropBase implements IMessage {

    private int x, y, z;
    private String player;

    public MessageDropBase() {}

    public static class MessageHandlerDropBase implements IMessageHandler<MessageDropBase, IMessage> {

        @Override
        public IMessage onMessage(MessageDropBase message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            TileEntity te = world.getTileEntity(message.getX(), message.getY(), message.getZ());
            TurretBase base = null;
            if (te instanceof TurretBase) {
                base = (TurretBase) te;
            }
            if (base != null) {
                TrustedPlayer trustedPlayer = PlayerUtil.getTrustedPlayer(player, base);
                if (PlayerUtil.isPlayerOwner(player, base) || (trustedPlayer != null && trustedPlayer.admin)) {
                    world.func_147480_a(message.getX(), message.getY(), message.getZ(), true);
                }
            }
            return null;
        }
    }

    public MessageDropBase(int x, int y, int z) {
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

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }

    public String getOwner() {
        return player;
    }
}

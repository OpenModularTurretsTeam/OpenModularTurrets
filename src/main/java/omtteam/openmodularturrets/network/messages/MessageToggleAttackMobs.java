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
import omtteam.omlib.api.tile.IHasTargetingSettings;
import omtteam.omlib.util.player.PlayerUtil;

@SuppressWarnings("unused")
public class MessageToggleAttackMobs implements IMessage {
    private int x, y, z;
    private boolean attack_mobs;

    public MessageToggleAttackMobs() {
    }

    public MessageToggleAttackMobs(int x, int y, int z, boolean attack_mobs) {
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

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    private int getZ() {
        return z;
    }

    private boolean doAttackMobs() {
        return attack_mobs;
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerToggleAttackMobs implements IMessageHandler<MessageToggleAttackMobs, IMessage> {
        @Override
        public IMessage onMessage(MessageToggleAttackMobs messageIn, MessageContext ctxIn) {
            final MessageToggleAttackMobs message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().player.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().player;
                TileEntity entity = world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
                IHasTargetingSettings machine = null;
                if (entity instanceof IHasTargetingSettings) {
                    machine = (IHasTargetingSettings) entity;
                }
                if (machine != null && PlayerUtil.canPlayerChangeSetting(player, machine)) {
                    machine.getTargetingSettings().setTargetMobs(message.doAttackMobs());
                }
            });
            return null;
        }
    }
}

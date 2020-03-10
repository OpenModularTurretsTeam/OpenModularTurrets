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
import omtteam.omlib.util.player.PlayerUtil;
import omtteam.openmodularturrets.tileentity.TurretBase;

@SuppressWarnings("unused")
public class MessageAdjustLightValue implements IMessage {
    private int x, y, z;
    private int value;

    public MessageAdjustLightValue() {
    }

    public MessageAdjustLightValue(int x, int y, int z, int value) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.value = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.value);
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

    public int getValue() {
        return value;
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerAdjustLightValue implements IMessageHandler<MessageAdjustLightValue, IMessage> {
        @Override
        public IMessage onMessage(MessageAdjustLightValue messageIn, MessageContext ctxIn) {
            final MessageAdjustLightValue message = messageIn;
            final MessageContext ctx = ctxIn;
            ((WorldServer) ctx.getServerHandler().player.getEntityWorld()).addScheduledTask(() -> {
                World world = ctx.getServerHandler().player.getEntityWorld();
                EntityPlayerMP player = ctx.getServerHandler().player;
                BlockPos pos = new BlockPos(message.getX(), message.getY(), message.getZ());
                TileEntity entity = world.getTileEntity(pos);
                TurretBase machine = null;
                if (entity instanceof TurretBase) {
                    machine = (TurretBase) entity;
                }
                if (machine != null && PlayerUtil.isPlayerAdmin(player, machine)) {
                    machine.getCamoSettings().setLightValue(message.value);
                    machine.informUpdate();
                    machine.updateCamoSettingsToPlayers();
                }
            });
            return null;
        }
    }
}

package omtteam.openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import omtteam.openmodularturrets.tileentity.turrets.AbstractDirectedTurret;

import static omtteam.omlib.proxy.ClientProxy.getWorld;
import static omtteam.openmodularturrets.compatibility.valkyrienwarfare.VWUtil.getTransformedBlockPos;

@SuppressWarnings("unused")
public class MessageUpdateTurret implements IMessage {
    private int x, y, z;
    private float yaw, pitch;

    public MessageUpdateTurret() {
    }

    public MessageUpdateTurret(AbstractDirectedTurret turretHead) {
        BlockPos pos = getTransformedBlockPos(turretHead);

        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.yaw = turretHead.getYaw();
        this.pitch = turretHead.getPitch();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.yaw = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    @SuppressWarnings("ConstantConditions")
    public static class MessageHandlerUpdateTurret implements IMessageHandler<MessageUpdateTurret, IMessage> {
        @Override
        public IMessage onMessage(MessageUpdateTurret messageIn, MessageContext ctxIn) {
            final MessageUpdateTurret message = messageIn;
            final MessageContext ctx = ctxIn;
            Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntity tileEntity = getWorld(FMLClientHandler.instance().getClient()).getTileEntity(
                        new BlockPos(message.x, message.y, message.z));

                AbstractDirectedTurret turret = null;
                if (tileEntity instanceof AbstractDirectedTurret) {
                    turret = (AbstractDirectedTurret) tileEntity;
                }
                if (turret != null) {
                    turret.setYaw(message.yaw);
                    turret.setPitch(message.pitch);
                }
            });
            return null;
        }
    }
}

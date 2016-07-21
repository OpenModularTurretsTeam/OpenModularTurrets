package openmodularturrets.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.tileentity.turretbase.TurretBase;



public class MessageToggleAttackMobs implements IMessage {
    private int x, y, z;
    private boolean attack_mobs;

    public MessageToggleAttackMobs() {
    }

    public static class MessageHandlerToggleAttackMobs implements IMessageHandler<MessageToggleAttackMobs, IMessage> {
        @Override
        public IMessage onMessage(MessageToggleAttackMobs message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));

            turret.setAttacksMobs(message.doAttackMobs());
            return null;
        }
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
}

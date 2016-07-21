package openmodularturrets.network.messages;


import io.netty.buffer.ByteBuf;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class MessageToggleAttackNeutralMobs implements IMessage {
    private int x, y, z;
    private boolean attack_neutrals;

    public MessageToggleAttackNeutralMobs() {
    }

    public static class MessageHandlerToggleAttackNeutralMobs implements IMessageHandler<MessageToggleAttackNeutralMobs, IMessage> {
        @Override
        public IMessage onMessage(MessageToggleAttackNeutralMobs message, MessageContext ctx) {
            World world = ctx.getServerHandler().playerEntity.worldObj;
            TurretBase turret = (TurretBase) world.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));

            turret.setAttacksNeutrals(message.doAttackNeutrals());
            return message;
        }

    }

    public MessageToggleAttackNeutralMobs(int x, int y, int z, boolean attack_neutrals) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.attack_neutrals = attack_neutrals;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();

        this.attack_neutrals = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);

        buf.writeBoolean(this.attack_neutrals);
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

    private boolean doAttackNeutrals() {
        return attack_neutrals;
    }
}

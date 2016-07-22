package openmodularturrets.network.messages;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import openmodularturrets.util.TrustedPlayer;
import openmodularturrets.tileentity.TurretBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Keridos on 05.10.14.
 * This Class is the Message that the electric floodlights TileEntity uses.
 */
public class MessageTurretBase implements IMessage {
    private int x, y, z, rfStorage;
    private boolean attacksMobs, attacksNeutrals, attacksPlayers, multiTargeting;
    private String owner, ownerName;
    private List<TrustedPlayer> trustedPlayers = new ArrayList<>();
    private ItemStack camoStack;

    public MessageTurretBase() {
    }

    public static class MessageHandlerTurretBase implements IMessageHandler<MessageTurretBase, IMessage> {
        @Override
        public IMessage onMessage(MessageTurretBase messageIn, MessageContext ctx) {
            final MessageTurretBase message = messageIn;
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {

                    TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(new BlockPos(message.x, message.y,
                            message.z));
                    if (tileEntity instanceof TurretBase) {
                        ((TurretBase) tileEntity).setOwner(message.owner);
                        ((TurretBase) tileEntity).setOwnerName(message.ownerName);
                        ((TurretBase) tileEntity).setEnergyStored(message.rfStorage);
                        ((TurretBase) tileEntity).setAttacksMobs(message.attacksMobs);
                        ((TurretBase) tileEntity).setAttacksNeutrals(message.attacksNeutrals);
                        ((TurretBase) tileEntity).setAttacksPlayers(message.attacksPlayers);
                        ((TurretBase) tileEntity).setMultiTargeting(message.multiTargeting);
                        ((TurretBase) tileEntity).setTrustedPlayers(message.trustedPlayers);
                        ((TurretBase) tileEntity).camoStack = message.camoStack;

                    }
                }
            });
            return null;
        }
    }


    public MessageTurretBase(TileEntity tileEntity) {
        if (tileEntity instanceof TurretBase) {
            TurretBase TurretBase = (TurretBase) tileEntity;
            this.x = TurretBase.getPos().getX();
            this.y = TurretBase.getPos().getY();
            this.z = TurretBase.getPos().getZ();
            this.owner = TurretBase.getOwner();
            this.ownerName = TurretBase.getOwnerName();
            this.rfStorage = TurretBase.getEnergyStored(EnumFacing.DOWN);
            this.attacksMobs = TurretBase.isAttacksMobs();
            this.attacksNeutrals = TurretBase.isAttacksNeutrals();
            this.attacksPlayers = TurretBase.isAttacksPlayers();
            this.multiTargeting = TurretBase.isMultiTargeting();
            this.trustedPlayers = TurretBase.getTrustedPlayers();
            this.camoStack = TurretBase.camoStack;
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        int ownerLength = buf.readInt();
        this.owner = new String(buf.readBytes(ownerLength).array());
        int ownerNameLength = buf.readInt();
        this.ownerName = new String(buf.readBytes(ownerNameLength).array());
        this.rfStorage = buf.readInt();
        this.attacksMobs = buf.readBoolean();
        this.attacksNeutrals = buf.readBoolean();
        this.attacksPlayers = buf.readBoolean();
        this.multiTargeting = buf.readBoolean();
        this.camoStack = ByteBufUtils.readItemStack(buf);
        int lengthOfTPList = buf.readInt();
        if (lengthOfTPList > 0) {
            for (int i = 0; i < lengthOfTPList; i++) {
                int length = buf.readInt();
                String name = new String(buf.readBytes(length).array());
                TrustedPlayer trustedPlayer = new TrustedPlayer(name);
                length = buf.readInt();
                trustedPlayer.uuid = UUID.fromString(new String(buf.readBytes(length).array()));
                trustedPlayer.canOpenGUI = buf.readBoolean();
                trustedPlayer.canChangeTargeting = buf.readBoolean();
                trustedPlayer.admin = buf.readBoolean();
                trustedPlayers.add(trustedPlayer);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(owner.length());
        buf.writeBytes(owner.getBytes());
        buf.writeInt(ownerName.length());
        buf.writeBytes(ownerName.getBytes());
        buf.writeInt(rfStorage);
        buf.writeBoolean(attacksMobs);
        buf.writeBoolean(attacksNeutrals);
        buf.writeBoolean(attacksPlayers);
        buf.writeBoolean(multiTargeting);
        ByteBufUtils.writeItemStack(buf, camoStack);
        buf.writeInt(trustedPlayers.size());
        if (trustedPlayers.size() > 0) {
            for (TrustedPlayer trustedPlayer : trustedPlayers) {
                buf.writeInt(trustedPlayer.getName().length());
                buf.writeBytes(trustedPlayer.getName().getBytes());
                buf.writeInt(trustedPlayer.uuid.toString().length());
                buf.writeBytes(trustedPlayer.uuid.toString().getBytes());
                buf.writeBoolean(trustedPlayer.canOpenGUI);
                buf.writeBoolean(trustedPlayer.canChangeTargeting);
                buf.writeBoolean(trustedPlayer.admin);
            }
        }
    }

    @Override
    public String toString() {
        return String.format(
                "MessageTurretBase - x:%s, y:%s, z:%s, owner:%s, rfstorage:%s", x, y, z, owner, rfStorage);
    }
}


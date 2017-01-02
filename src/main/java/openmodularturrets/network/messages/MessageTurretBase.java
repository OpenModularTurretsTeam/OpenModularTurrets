package openmodularturrets.network.messages;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModularTurrets;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Keridos on 05.10.14.
 * This Class is the Message that the electric floodlights TileEntity uses.
 */
public class MessageTurretBase implements IMessage {
    private int x, y, z, rfStorage, yAxisDetect;
    private boolean attacksMobs, attacksNeutrals, attacksPlayers, multiTargeting, waitForTrustedPlayer;
    private String owner, ownerName;
    private List<TrustedPlayer> trustedPlayers = new ArrayList<>();
    private ItemStack camoStack;

    public MessageTurretBase() {
    }

    public static class MessageHandlerTurretBase implements IMessageHandler<MessageTurretBase, IMessage> {
        @Override
        public IMessage onMessage(MessageTurretBase message, MessageContext ctx) {
            TileEntity tileEntity = ModularTurrets.proxy.getWorld().getTileEntity(message.x, message.y, message.z);
            if (tileEntity instanceof TurretBase) {
                ((TurretBase) tileEntity).setOwner(message.owner);
                ((TurretBase) tileEntity).setOwnerName(message.ownerName);
                ((TurretBase) tileEntity).setEnergyStored(message.rfStorage);
                ((TurretBase) tileEntity).setyAxisDetect(message.yAxisDetect);
                ((TurretBase) tileEntity).setAttacksMobs(message.attacksMobs);
                ((TurretBase) tileEntity).setAttacksNeutrals(message.attacksNeutrals);
                ((TurretBase) tileEntity).setAttacksPlayers(message.attacksPlayers);
                ((TurretBase) tileEntity).setMultiTargeting(message.multiTargeting);
                ((TurretBase) tileEntity).setTrustedPlayers(message.trustedPlayers);
                ((TurretBase) tileEntity).waitForTrustedPlayer = message.waitForTrustedPlayer;
                ((TurretBase) tileEntity).camoStack = message.camoStack;
            }
            return null;
        }
    }

    public MessageTurretBase(TileEntity tileEntity) {
        if (tileEntity instanceof TurretBase) {
            TurretBase TurretBase = (TurretBase) tileEntity;
            this.x = TurretBase.xCoord;
            this.y = TurretBase.yCoord;
            this.z = TurretBase.zCoord;
            this.owner = TurretBase.getOwner();
            this.ownerName = TurretBase.getOwnerName();
            this.rfStorage = TurretBase.getEnergyStored(ForgeDirection.UNKNOWN);
            this.yAxisDetect = TurretBase.getyAxisDetect();
            this.attacksMobs = TurretBase.isAttacksMobs();
            this.attacksNeutrals = TurretBase.isAttacksNeutrals();
            this.attacksPlayers = TurretBase.isAttacksPlayers();
            this.multiTargeting = TurretBase.isMultiTargeting();
            this.trustedPlayers = TurretBase.getTrustedPlayers();
            this.waitForTrustedPlayer = TurretBase.waitForTrustedPlayer;
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
        this.yAxisDetect = buf.readInt();
        this.attacksMobs = buf.readBoolean();
        this.attacksNeutrals = buf.readBoolean();
        this.attacksPlayers = buf.readBoolean();
        this.multiTargeting = buf.readBoolean();
        this.waitForTrustedPlayer = buf.readBoolean();
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
        buf.writeInt(yAxisDetect);
        buf.writeBoolean(attacksMobs);
        buf.writeBoolean(attacksNeutrals);
        buf.writeBoolean(attacksPlayers);
        buf.writeBoolean(multiTargeting);
        buf.writeBoolean(waitForTrustedPlayer);
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
}


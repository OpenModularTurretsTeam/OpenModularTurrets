package omtteam.openmodularturrets.network.messages;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.util.TrustedPlayer;
import omtteam.openmodularturrets.tileentity.TurretBase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static omtteam.omlib.proxy.ClientProxy.getWorld;


/**
 * Created by Keridos on 05.10.14.
 * This Class is the Message that the electric floodlights TileEntity uses.
 */
@SuppressWarnings("unused")
public class MessageTurretBase implements IMessage {
    private int x, y, z, rfStorageCurrent, rfStorageMax, tier, camoBlockMeta, maxRange;
    private boolean attacksMobs, attacksNeutrals, attacksPlayers, multiTargeting;
    private String owner, ownerName, camoBlockRegName;
    private List<TrustedPlayer> trustedPlayers = new ArrayList<>();

    public MessageTurretBase() {
    }


    public static class MessageHandlerTurretBase implements IMessageHandler<MessageTurretBase, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings("deprecation")
        public IMessage onMessage(MessageTurretBase messageIn, MessageContext ctx) {
            final MessageTurretBase message = messageIn;
            Minecraft.getMinecraft().addScheduledTask(() -> {

                TileEntity tileEntity = getWorld(FMLClientHandler.instance().getClient()).getTileEntity(new BlockPos(message.x, message.y,
                        message.z));
                if (tileEntity instanceof TurretBase) {
                    TurretBase base = (TurretBase) tileEntity;
                    OMEnergyStorage storage = (OMEnergyStorage) base.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
                    base.setOwner(message.owner);
                    base.setOwnerName(message.ownerName);
                    if (storage != null) {
                        storage.setEnergyStored(message.rfStorageCurrent);
                        storage.setCapacity(message.rfStorageMax);
                    }
                    base.setAttacksMobs(message.attacksMobs);
                    base.setAttacksNeutrals(message.attacksNeutrals);
                    base.setAttacksPlayers(message.attacksPlayers);
                    base.setMultiTargeting(message.multiTargeting);
                    base.setTrustedPlayers(message.trustedPlayers);
                    base.setTier(message.tier);
                    base.setCamoState(ForgeRegistries.BLOCKS.getValue(
                            new ResourceLocation(message.camoBlockRegName)).getStateFromMeta(message.camoBlockMeta));
                    base.setCurrentMaxRange(message.maxRange);
                }
            });
            return null;
        }

    }

    public MessageTurretBase(TileEntity tileEntity) {
        if (tileEntity instanceof TurretBase) {
            TurretBase base = (TurretBase) tileEntity;
            this.x = base.getPos().getX();
            this.y = base.getPos().getY();
            this.z = base.getPos().getZ();
            this.tier = base.getTier();
            this.owner = base.getOwner();
            this.ownerName = base.getOwnerName();
            this.rfStorageCurrent = base.getEnergyLevel(EnumFacing.DOWN);
            this.rfStorageMax = base.getMaxEnergyLevel(EnumFacing.DOWN);
            this.attacksMobs = base.isAttacksMobs();
            this.attacksNeutrals = base.isAttacksNeutrals();
            this.attacksPlayers = base.isAttacksPlayers();
            this.multiTargeting = base.isMultiTargeting();
            this.trustedPlayers = base.getTrustedPlayers();
            this.camoBlockRegName = base.getCamoState().getBlock().getRegistryName().toString();
            this.camoBlockMeta = base.getCamoState().getBlock().getMetaFromState(base.getCamoState());
            this.maxRange = base.getCurrentMaxRange();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.tier = buf.readInt();
        int ownerLength = buf.readInt();
        this.owner = new String(buf.readBytes(ownerLength).array());
        int ownerNameLength = buf.readInt();
        this.ownerName = new String(buf.readBytes(ownerNameLength).array());
        this.rfStorageCurrent = buf.readInt();
        this.rfStorageMax = buf.readInt();
        this.maxRange = buf.readInt();
        this.attacksMobs = buf.readBoolean();
        this.attacksNeutrals = buf.readBoolean();
        this.attacksPlayers = buf.readBoolean();
        this.multiTargeting = buf.readBoolean();
        int camoBlockRegNameLength = buf.readInt();
        this.camoBlockRegName = new String(buf.readBytes(camoBlockRegNameLength).array());
        this.camoBlockMeta = buf.readInt();
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
        buf.writeInt(tier);
        buf.writeInt(owner.length());
        buf.writeBytes(owner.getBytes());
        buf.writeInt(ownerName.length());
        buf.writeBytes(ownerName.getBytes());
        buf.writeInt(rfStorageCurrent);
        buf.writeInt(rfStorageMax);
        buf.writeInt(maxRange);
        buf.writeBoolean(attacksMobs);
        buf.writeBoolean(attacksNeutrals);
        buf.writeBoolean(attacksPlayers);
        buf.writeBoolean(multiTargeting);
        buf.writeInt(camoBlockRegName.length());
        buf.writeBytes(camoBlockRegName.getBytes());
        buf.writeInt(camoBlockMeta);
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
                "MessageTurretBase - x:%s, y:%s, z:%s, owner:%s, rfstorage:%s", x, y, z, owner, rfStorageCurrent);
    }
}


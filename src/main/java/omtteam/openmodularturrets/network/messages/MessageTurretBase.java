package omtteam.openmodularturrets.network.messages;


import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import omtteam.omlib.power.OMEnergyStorage;
import omtteam.omlib.tileentity.EnumMachineMode;
import omtteam.omlib.util.EnumAccessMode;
import omtteam.omlib.util.player.TrustedPlayer;
import omtteam.openmodularturrets.tileentity.TurretBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static omtteam.omlib.proxy.ClientProxy.getWorld;


/**
 * Created by Keridos on 05.10.14.
 * This Class is the Message that the electric floodlights TileEntity uses.
 */
@SuppressWarnings("unused")
public class MessageTurretBase implements IMessage {
    private int x, y, z, rfStorageCurrent, rfStorageMax, tier, camoBlockMeta, maxRange, kills, playerKills, lightValue, lightOpacity;
    private boolean attacksMobs, attacksNeutrals, attacksPlayers, multiTargeting;
    private String owner, ownerName, camoBlockRegName;
    private List<TrustedPlayer> trustedPlayers = new ArrayList<>();
    private EnumMachineMode mode;

    public MessageTurretBase() {
    }


    public MessageTurretBase(TileEntity tileEntity) {
        if (tileEntity instanceof TurretBase) {
            TurretBase base = (TurretBase) tileEntity;
            this.x = base.getPos().getX();
            this.y = base.getPos().getY();
            this.z = base.getPos().getZ();
            this.tier = base.getTier();
            this.lightValue = base.getCamoSettings().getLightValue();
            this.lightOpacity = base.getCamoSettings().getLightOpacity();
            this.owner = base.getOwner();
            this.ownerName = base.getOwnerName();
            this.rfStorageCurrent = base.getEnergyLevel(EnumFacing.DOWN);
            this.rfStorageMax = base.getMaxEnergyLevel(EnumFacing.DOWN);
            this.attacksMobs = base.isAttacksMobs();
            this.attacksNeutrals = base.isAttacksNeutrals();
            this.attacksPlayers = base.isAttacksPlayers();
            this.multiTargeting = base.isMultiTargeting();
            this.trustedPlayers = base.getTrustedPlayers();
            this.camoBlockRegName = Objects.requireNonNull(base.getCamoState().getBlock().getRegistryName()).toString();
            this.camoBlockMeta = base.getCamoState().getBlock().getMetaFromState(base.getCamoState());
            this.maxRange = base.getCurrentMaxRange();
            this.mode = base.getMode();
            this.kills = base.getKills();
            this.playerKills = base.getPlayerKills();
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.tier = buf.readInt();
        this.lightValue = buf.readInt();
        this.lightOpacity = buf.readInt();
        this.owner = ByteBufUtils.readUTF8String(buf);
        this.ownerName = ByteBufUtils.readUTF8String(buf);
        this.rfStorageCurrent = buf.readInt();
        this.rfStorageMax = buf.readInt();
        this.maxRange = buf.readInt();
        this.kills = buf.readInt();
        this.playerKills = buf.readInt();
        this.mode = EnumMachineMode.values()[buf.readInt()];
        this.attacksMobs = buf.readBoolean();
        this.attacksNeutrals = buf.readBoolean();
        this.attacksPlayers = buf.readBoolean();
        this.multiTargeting = buf.readBoolean();
        this.camoBlockRegName = ByteBufUtils.readUTF8String(buf);
        this.camoBlockMeta = buf.readInt();
        int lengthOfTPList = buf.readInt();
        if (lengthOfTPList > 0) {
            for (int i = 0; i < lengthOfTPList; i++) {
                String name = ByteBufUtils.readUTF8String(buf);
                TrustedPlayer trustedPlayer = new TrustedPlayer(name);
                trustedPlayer.setUuid(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
                trustedPlayer.setAccessMode(EnumAccessMode.values()[buf.readInt()]);
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
        buf.writeInt(lightValue);
        buf.writeInt(lightOpacity);
        ByteBufUtils.writeUTF8String(buf, owner);
        ByteBufUtils.writeUTF8String(buf, ownerName);
        buf.writeInt(rfStorageCurrent);
        buf.writeInt(rfStorageMax);
        buf.writeInt(maxRange);
        buf.writeInt(kills);
        buf.writeInt(playerKills);
        buf.writeInt(mode.ordinal());
        buf.writeBoolean(attacksMobs);
        buf.writeBoolean(attacksNeutrals);
        buf.writeBoolean(attacksPlayers);
        buf.writeBoolean(multiTargeting);
        ByteBufUtils.writeUTF8String(buf, camoBlockRegName);
        buf.writeInt(camoBlockMeta);
        buf.writeInt(trustedPlayers.size());
        if (trustedPlayers.size() > 0) {
            for (TrustedPlayer trustedPlayer : trustedPlayers) {
                ByteBufUtils.writeUTF8String(buf, trustedPlayer.getName());
                ByteBufUtils.writeUTF8String(buf, trustedPlayer.getUuid().toString());
                buf.writeInt(trustedPlayer.getAccessMode().ordinal());
            }
        }
    }

    public static class MessageHandlerTurretBase implements IMessageHandler<MessageTurretBase, IMessage> {
        @Override
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
                    base.getCamoSettings().setLightValue(message.lightValue);
                    base.getCamoSettings().setLightOpacity(message.lightOpacity);
                    base.setMode(message.mode);
                    base.setCamoState(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(
                            new ResourceLocation(message.camoBlockRegName))).getStateFromMeta(message.camoBlockMeta));
                    base.setCurrentMaxRange(message.maxRange);
                    base.setKills(message.kills);
                    base.setPlayerKills(message.playerKills);
                }
            });
            return null;
        }

    }

    @Override
    public String toString() {
        return String.format(
                "MessageTurretBase - x:%s, y:%s, z:%s, owner:%s, rfstorage:%s", x, y, z, owner, rfStorageCurrent);
    }
}


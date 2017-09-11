package openmodularturrets.tileentity.turretbase;

import api.undercurrent.iface.IUCTile;
import api.undercurrent.iface.UCTileDefinition;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.compatability.ModCompatibility;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.network.messages.MessageTurretBase;
import openmodularturrets.tileentity.TileEntityContainer;
import openmodularturrets.ucdefinitions.TurretBaseUCDefinition;
import openmodularturrets.util.MathUtil;
import openmodularturrets.util.TurretHeadUtil;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.visnet.VisNetHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static openmodularturrets.util.PlayerUtil.*;

@Optional.InterfaceList({
        @Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "ComputerCraft"),
        @Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers"),
        @Optional.Interface(iface = "thaumcraft.api.aspects.IAspectContainer", modid = "Thaumcraft"),
        @Optional.Interface(iface = "thaumcraft.api.aspects.IEssentiaTransport", modid = "Thaumcraft"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "IC2")})

public abstract class TurretBase extends TileEntityContainer implements IEnergyHandler, SimpleComponent, ISidedInventory, IEssentiaTransport, IAspectContainer, IPeripheral, IEnergySink, IUCTile {
    public int trustedPlayerIndex = 0;
    public ItemStack camoStack;

    //For concealment
    public boolean shouldConcealTurrets;

    //For multiTargeting
    private boolean multiTargeting = false;

    private final EnergyStorage storage;
    private int yAxisDetect;
    private boolean attacksMobs;
    private boolean attacksNeutrals;
    private boolean attacksPlayers;
    private String owner = "";
    private String ownerName = "";
    private List<TrustedPlayer> trustedPlayers;
    private int ticks;
    private boolean active;
    private boolean inverted;
    private boolean redstone;
    private boolean checkRedstone = false;
    private boolean computerAccessible = false;
    private float amountOfPotentia = 0F;
    private final float maxAmountOfPotentia = ConfigHandler.getPotentiaAddonCapacity();
    private ArrayList<IComputerAccess> comp;
    private double storageEU;
    private boolean wasAddedToEnergyNet = false;
    public boolean waitForTrustedPlayer = false;

    public TurretBase(int MaxEnergyStorage, int MaxIO) {
        super();
        this.yAxisDetect = 2;
        this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
        this.attacksMobs = true;
        this.attacksNeutrals = true;
        this.attacksPlayers = false;
        this.trustedPlayers = new ArrayList<>();
        this.inv = new ItemStack[this.getSizeInventory()];
        this.inverted = true;
        this.active = true;
        this.ticks = 0;
    }

    private static void updateRedstoneReactor(TurretBase base) {
        if (!TurretHeadUtil.hasRedstoneReactor(base)) {
            return;
        }

        if (ConfigHandler.getRedstoneReactorAddonGen() < (base.getMaxEnergyStored(
                ForgeDirection.UNKNOWN) - base.getEnergyStored(ForgeDirection.UNKNOWN))) {

            //Prioritise redstone blocks
            ItemStack redstoneBlock = TurretHeadUtil.useSpecificItemStackBlockFromBase(base, new ItemStack(
                    Blocks.redstone_block));

            if (redstoneBlock == null) {
                redstoneBlock = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorldObj(),
                        new ItemStack(Blocks.redstone_block),
                        base);
            }

            if (redstoneBlock != null && ConfigHandler.getRedstoneReactorAddonGen() * 9 < (base.getMaxEnergyStored(
                    ForgeDirection.UNKNOWN) - base.getEnergyStored(ForgeDirection.UNKNOWN))) {
                base.storage.modifyEnergyStored(ConfigHandler.getRedstoneReactorAddonGen() * 9);
                return;
            }

            ItemStack redstone = TurretHeadUtil.useSpecificItemStackItemFromBase(base, Items.redstone);

            if (redstone == null) {
                redstone = TurretHeadUtil.getSpecificItemFromInvExpanders(base.getWorldObj(),
                        new ItemStack(Items.redstone), base);
            }

            if (redstone != null) {
                base.storage.modifyEnergyStored(ConfigHandler.getRedstoneReactorAddonGen());
            }
        }
    }

    @Optional.Method(modid = "IC2")
    @Override
    public double injectEnergy(ForgeDirection forgeDirection, double v, double v1) {
        storageEU += v;
        return 0.0D;
    }

    @Optional.Method(modid = "IC2")
    @Override
    public int getSinkTier() {
        return 4;
    }

    @Optional.Method(modid = "IC2")
    @Override
    public double getDemandedEnergy() {
        return Math.max(4000D - storageEU, 0.0D);
    }

    @Optional.Method(modid = "IC2")
    @Override
    public boolean acceptsEnergyFrom(TileEntity tileEntity, ForgeDirection forgeDirection) {
        return true;
    }

    @Optional.Method(modid = "IC2")
    protected void addToIc2EnergyNetwork() {
        if (!worldObj.isRemote) {
            EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
        }
    }

    @Optional.Method(modid = "IC2")
    protected void removeFromIc2EnergyNetwork() {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        onChunkUnload();
    }

    @Override
    public void onChunkUnload() {
        if (wasAddedToEnergyNet &&
                ModCompatibility.IC2Loaded) {
            removeFromIc2EnergyNetwork();

            wasAddedToEnergyNet = false;
        }
    }

    private int getMaxEnergyStorageWithExtenders() {
        int tier = getBaseTier();
        switch (tier) {
            case 1:
                return ConfigHandler.getBaseTierOneMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            case 2:
                return ConfigHandler.getBaseTierTwoMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            case 3:
                return ConfigHandler.getBaseTierThreeMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            case 4:
                return ConfigHandler.getBaseTierFourMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            case 5:
                return ConfigHandler.getBaseTierFiveMaxCharge() + TurretHeadUtil.getPowerExpanderTotalExtraCapacity(
                        this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        return 0;
    }

    public boolean addTrustedPlayer(String name) {
        waitForTrustedPlayer = false;
        TrustedPlayer trustedPlayer = new TrustedPlayer(name);
        trustedPlayer.uuid = getPlayerUUID(name);
        if (!isPlayerNameValid(name)) {
            return false;
        }


        if (ConfigHandler.offlineModeSupport) {
            if (trustedPlayer.getName().equals(getOwnerName())) {
                return false;
            }

        } else {
            if (trustedPlayer.uuid == null || trustedPlayer.uuid.toString().equals(getOwnerName())) {
                return false;
            }
        }

        if (trustedPlayer.uuid != null || ConfigHandler.offlineModeSupport) {
            for (TrustedPlayer player : trustedPlayers) {
                if (ConfigHandler.offlineModeSupport) {
                    if (player.getName().toLowerCase().equals(name.toLowerCase()) || player.getName().equals(getOwnerName())) {
                        return false;
                    }
                } else {
                    if (player.getName().toLowerCase().equals(name.toLowerCase()) || trustedPlayer.uuid.toString().equals(
                            owner)) {
                        return false;
                    }
                }
            }
            trustedPlayers.add(trustedPlayer);
            return true;
        }
        return false;
    }


    public boolean removeTrustedPlayer(String name) {
        for (TrustedPlayer player : trustedPlayers) {
            if (player.getName().equals(name)) {
                trustedPlayers.remove(player);
                return true;
            }
        }
        return false;
    }

    public List<TrustedPlayer> getTrustedPlayers() {
        return trustedPlayers;
    }

    public TrustedPlayer getTrustedPlayer(String name) {
        for (TrustedPlayer trustedPlayer : trustedPlayers) {
            if (trustedPlayer.name.equals(name)) {
                return trustedPlayer;
            }
        }
        return null;
    }

    public TrustedPlayer getTrustedPlayer(UUID uuid) {
        for (TrustedPlayer trustedPlayer : trustedPlayers) {
            if (trustedPlayer.uuid.equals(uuid)) {
                return trustedPlayer;
            }
        }
        return null;
    }

    public void setTrustedPlayers(List<TrustedPlayer> list) {
        this.trustedPlayers = list;
    }

    private NBTTagList getTrustedPlayersAsNBT() {
        NBTTagList nbt = new NBTTagList();
        for (TrustedPlayer trustedPlayer : trustedPlayers) {
            NBTTagCompound nbtPlayer = new NBTTagCompound();
            nbtPlayer.setString("name", trustedPlayer.name);
            nbtPlayer.setBoolean("canOpenGUI", trustedPlayer.canOpenGUI);
            nbtPlayer.setBoolean("canChangeTargeting", trustedPlayer.canChangeTargeting);
            nbtPlayer.setBoolean("admin", trustedPlayer.admin);
            if (trustedPlayer.uuid != null) {
                nbtPlayer.setString("UUID", trustedPlayer.uuid.toString());
            } else if (getPlayerUUID(trustedPlayer.name) != null) {
                nbtPlayer.setString("UUID", getPlayerUUID(trustedPlayer.name).toString());
            }
            nbt.appendTag(nbtPlayer);
        }
        return nbt;
    }

    private void buildTrustedPlayersFromNBT(NBTTagList nbt) {
        trustedPlayers.clear();
        for (int i = 0; i < nbt.tagCount(); i++) {
            if (!nbt.getCompoundTagAt(i).getString("name").equals("")) {
                NBTTagCompound nbtPlayer = nbt.getCompoundTagAt(i);
                TrustedPlayer trustedPlayer = new TrustedPlayer(nbtPlayer.getString("name"));
                trustedPlayer.canOpenGUI = nbtPlayer.getBoolean("canOpenGUI");
                trustedPlayer.canChangeTargeting = nbtPlayer.getBoolean("canChangeTargeting");
                trustedPlayer.admin = nbtPlayer.getBoolean("admin");
                if (nbtPlayer.hasKey("UUID")) {
                    trustedPlayer.uuid = getPlayerUIDUnstable(nbtPlayer.getString("UUID"));
                } else {
                    trustedPlayer.uuid = getPlayerUUID(trustedPlayer.name);
                }
                if (trustedPlayer.uuid != null) {
                    trustedPlayers.add(trustedPlayer);
                }
            } else if (nbt.getCompoundTagAt(i).getString("name").equals("")) {
                TrustedPlayer trustedPlayer = new TrustedPlayer(nbt.getStringTagAt(i));
                Logger.getGlobal().info("found legacy trusted Player: " + nbt.getStringTagAt(i));
                trustedPlayer.uuid = getPlayerUUID(trustedPlayer.name);
                if (trustedPlayer.uuid != null) {
                    trustedPlayers.add(trustedPlayer);
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setInteger("maxStorage", this.storage.getMaxEnergyStored());
        par1.setInteger("energyStored", this.getEnergyStored(ForgeDirection.UNKNOWN));
        par1.setFloat("amountOfPotentia", amountOfPotentia);
        par1.setInteger("maxIO", this.storage.getMaxReceive());
        par1.setInteger("yAxisDetect", this.yAxisDetect);
        par1.setBoolean("attacksMobs", attacksMobs);
        par1.setBoolean("attacksNeutrals", attacksNeutrals);
        par1.setBoolean("attacksPlayers", attacksPlayers);
        if (ConfigHandler.offlineModeSupport && !ownerName.isEmpty()) {
            par1.setString("owner", ownerName);
        } else if (!ConfigHandler.offlineModeSupport){
            par1.setString("owner", owner);
        }
        if (ownerName.isEmpty() && getPlayerNameFromUUID(owner) != null) {
            ownerName = getPlayerNameFromUUID(owner);
        }
        par1.setString("ownerName", ownerName);
        par1.setTag("trustedPlayers", getTrustedPlayersAsNBT());
        par1.setBoolean("active", active);
        par1.setBoolean("inverted", inverted);
        par1.setBoolean("redstone", redstone);
        par1.setBoolean("computerAccessible", computerAccessible);
        par1.setBoolean("shouldConcealTurrets", shouldConcealTurrets);
        par1.setBoolean("multiTargeting", multiTargeting);
        par1.setDouble("storageEU", storageEU);

        NBTTagList itemList = new NBTTagList();

        for (int i = 0; i < this.inv.length; i++) {
            ItemStack stack = this.getStackInSlot(i);
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }
        par1.setTag("Inventory", itemList);

        if (camoStack != null) {
            NBTTagCompound tag2 = new NBTTagCompound();
            camoStack.writeToNBT(tag2);
            par1.setTag("CamoStack", tag2);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);

        this.storage.setCapacity(par1.getInteger("maxStorage"));
        this.storage.setEnergyStored(par1.getInteger("energyStored"));
        this.storage.setMaxReceive(par1.getInteger("maxIO"));
        this.amountOfPotentia = par1.getFloat("amountOfPotentia");
        this.yAxisDetect = par1.getInteger("yAxisDetect");
        this.attacksMobs = par1.getBoolean("attacksMobs");
        this.attacksNeutrals = par1.getBoolean("attacksNeutrals");
        this.attacksPlayers = par1.getBoolean("attacksPlayers");
        this.shouldConcealTurrets = par1.getBoolean("shouldConcealTurrets");
        this.multiTargeting = par1.getBoolean("multiTargeting");

        if (getPlayerUIDUnstable(par1.getString("owner")) != null) {
            this.owner = getPlayerUIDUnstable(par1.getString("owner")).toString();
        } else if (getPlayerUUID(par1.getString("owner")) != null) {
            this.owner = getPlayerUUID(par1.getString("owner")).toString();
        } else if (getPlayerUUID(par1.getString("ownerName")) != null) {
            this.owner = getPlayerUUID(par1.getString("ownerName")).toString();
        } else if (!ConfigHandler.offlineModeSupport) {
            Logger.getGlobal().info("Found non existent owner: " + par1.getString(
                    "owner") + "at coordinates: " + this.xCoord + "," + this.yCoord + "," + this.zCoord + ". Dropping Turretbase");
            worldObj.func_147480_a(this.xCoord, this.yCoord, this.zCoord, true);
            return;
        }
        if (par1.hasKey("ownerName")) {
            this.ownerName = par1.getString("ownerName");
        }
        buildTrustedPlayersFromNBT(par1.getTagList("trustedPlayers", 10));
        if (trustedPlayers.size() == 0) {
            buildTrustedPlayersFromNBT(par1.getTagList("trustedPlayers", 8));
        }
        this.active = !par1.hasKey("active") || par1.getBoolean("active");
        this.inverted = !par1.hasKey("inverted") || par1.getBoolean("inverted");
        if (par1.hasKey("redstone")) {
            this.redstone = par1.getBoolean("redstone");
        } else {
            checkRedstone = true;
        }
        this.computerAccessible = par1.hasKey("computerAccessible") && par1.getBoolean("computerAccessible");
        if (par1.hasKey("storageEU")) {
            this.storageEU = par1.getDouble("storageEU");
        } else {
            storageEU = 0;
        }

        NBTTagList tagList = par1.getTagList("Inventory", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < inv.length) {
                inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }

        NBTTagCompound tag2 = par1.getCompoundTag("CamoStack");
        if (tag2 != null) {
            camoStack = ItemStack.loadItemStackFromNBT(tag2);
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            if (stack.stackSize <= amt) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amt);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            setInventorySlotContents(slot, null);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5,
                yCoord + 0.5,
                zCoord + 0.5) < 64;
    }

    @Optional.Method(modid = "Thaumcraft")
    private IEssentiaTransport getConnectableTileWithoutOrientation() {
        if (worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
        }

        if (worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
        }

        if (worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
        }

        if (worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        }

        if (worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
        }

        if (worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof IEssentiaTransport) {
            return (IEssentiaTransport) worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
        }
        return null;
    }

    @Optional.Method(modid = "Thaumcraft")
    private int drawEssentia() {
        IEssentiaTransport ic = getConnectableTileWithoutOrientation();
        if (ic != null) {
            if (ic.takeEssentia(Aspect.ENERGY, 1, ForgeDirection.UP) == 1) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public void updateEntity() {
        if (ModCompatibility.IC2Loaded && ConfigHandler.EUSupport && !wasAddedToEnergyNet && !worldObj.isRemote) {
            addToIc2EnergyNetwork();
            wasAddedToEnergyNet = true;
        }

        ticks++;
        if (!worldObj.isRemote && ticks % 5 == 0) {

            //Concealment
            this.shouldConcealTurrets = TurretHeadUtil.hasConcealmentAddon(this);

            //Extenders
            this.storage.setCapacity(getMaxEnergyStorageWithExtenders());

            //Thaumcraft
            if (ModCompatibility.ThaumcraftLoaded && TurretHeadUtil.hasPotentiaUpgradeAddon(this)) {
                if (amountOfPotentia > 0.05F && !(storage.getMaxEnergyStored() - storage.getEnergyStored() == 0)) {
                    if (VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.ORDER, 5) == 5) {
                        this.amountOfPotentia = this.amountOfPotentia - 0.05F;
                        this.storage.modifyEnergyStored(Math.round(ConfigHandler.getPotentiaToRFRatio() * 5));
                    } else {
                        this.amountOfPotentia = this.amountOfPotentia - 0.05F;
                        this.storage.modifyEnergyStored(Math.round(ConfigHandler.getPotentiaToRFRatio() / 2));
                    }
                }
            }

            if (ModCompatibility.IC2Loaded && ConfigHandler.EUSupport) {
                if (storage.getMaxEnergyStored() != storage.getEnergyStored() && storageEU > 0) {
                    storage.modifyEnergyStored(MathUtil.truncateDoubleToInt(
                            Math.min(storage.getMaxEnergyStored() - storage.getEnergyStored(),
                                    storageEU * ConfigHandler.EUtoRFRatio)));
                    storageEU -= Math.min(
                            (storage.getMaxEnergyStored() - storage.getEnergyStored()) / ConfigHandler.EUtoRFRatio,
                            storageEU * ConfigHandler.EUtoRFRatio);
                }
            }

            if (ticks == 20) {

                //General
                ticks = 0;
                updateRedstoneReactor(this);

                //Thaumcraft
                if (ModCompatibility.ThaumcraftLoaded && amountOfPotentia <= maxAmountOfPotentia) {
                    amountOfPotentia = amountOfPotentia + drawEssentia();
                }

                //Computers
                this.computerAccessible = (ModCompatibility.OpenComputersLoaded || ModCompatibility.ComputercraftLoaded) && TurretHeadUtil.hasSerialPortAddon(
                        this);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        return NetworkingHandler.INSTANCE.getPacketFrom(new MessageTurretBase(this));
    }

    public abstract int getBaseTier();

    public boolean isAttacksMobs() {
        return attacksMobs;
    }

    public void setAttacksMobs(boolean attacksMobs) {
        this.attacksMobs = attacksMobs;
    }

    public boolean isAttacksNeutrals() {
        return attacksNeutrals;
    }

    public void setAttacksNeutrals(boolean attacksNeutrals) {
        this.attacksNeutrals = attacksNeutrals;
    }

    public boolean isAttacksPlayers() {
        return attacksPlayers;
    }

    public void setAttacksPlayers(boolean attacksPlayers) {
        this.attacksPlayers = attacksPlayers;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String name) {
        ownerName = name;
    }
    
    public boolean isMultiTargeting() {
        return multiTargeting;
    }

    public void setMultiTargeting(boolean multiTargeting) {
        this.multiTargeting = multiTargeting;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return storage.getMaxEnergyStored();
    }

    public void setEnergyStored(int energy) {
        storage.setEnergyStored(energy);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    public int getyAxisDetect() {
        return yAxisDetect;
    }

    public void setyAxisDetect(int yAxisDetect) {
        this.yAxisDetect = yAxisDetect;

        if (this.yAxisDetect > 9) {
            this.yAxisDetect = 9;
        }

        if (this.yAxisDetect < 0) {
            this.yAxisDetect = 0;
        }
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    }

    @Override
    public boolean canInsertItem(int slotID, ItemStack itemstack, int side) {
        return isItemValidForSlot(slotID, itemstack);
    }

    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side) {
        return true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean getInverted() {
        return this.inverted;
    }

    private void setInverted(boolean inverted) {
        this.inverted = inverted;
        this.active = redstone ^ this.inverted;
    }

    private boolean getRedstone() {
        return this.redstone;
    }

    public void setRedstone(boolean redstone) {
        this.redstone = redstone;
        this.active = this.redstone ^ inverted;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean isConnectable(ForgeDirection face) {
        return TurretHeadUtil.hasPotentiaUpgradeAddon(this);
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return TurretHeadUtil.hasPotentiaUpgradeAddon(this);
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return null;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        return null;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return 0;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public AspectList getAspects() {
        if (TurretHeadUtil.hasPotentiaUpgradeAddon(this)) {
            return new AspectList().add(Aspect.ENERGY, (int) Math.floor(amountOfPotentia));
        } else {
            return null;
        }
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public void setAspects(AspectList aspects) {
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return tag.equals(Aspect.ENERGY);
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int getSuctionAmount(ForgeDirection face) {
        return 64;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        return 0;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Optional.Method(modid = "Thaumcraft")
    @Override
    public int containerContains(Aspect tag) {
        if (tag.equals(Aspect.ENERGY)) {
            return Math.round(amountOfPotentia);
        }
        return 0;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():string; returns owner of turret base.")
    public Object[] getOwner(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getOwnerName()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack hostile mobs.")
    public Object[] isAttacksMobs(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksMobs()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean;  sets to attack hostile mobs or not.")
    public Object[] setAttacksMobs(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksMobs(args.checkBoolean(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack neutral mobs.")
    public Object[] isAttacksNeutrals(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksNeutrals()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; sets to attack neutral mobs or not.")
    public Object[] setAttacksNeutrals(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksNeutrals(args.checkBoolean(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack players.")
    public Object[] isAttacksPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isAttacksPlayers()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; sets to attack players or not.")
    public Object[] setAttacksPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setAttacksPlayers(args.checkBoolean(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():table; returns a table of trusted players on this base.")
    public Object[] getTrustedPlayers(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getTrustedPlayers()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function(name:String, [canOpenGUI:boolean , canChangeTargeting:boolean , " + "admin:boolean]):string; adds Trusted player to Trustlist.")
    public Object[] addTrustedPlayer(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        if (!this.addTrustedPlayer(args.checkString(0))) {
            return new Object[]{"Name not valid!"};
        }
        TrustedPlayer trustedPlayer = this.getTrustedPlayer(args.checkString(0));
        trustedPlayer.canOpenGUI = args.optBoolean(1, false);
        trustedPlayer.canChangeTargeting = args.optBoolean(1, false);
        trustedPlayer.admin = args.optBoolean(1, false);
        trustedPlayer.uuid = getPlayerUUID(args.checkString(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():string; removes Trusted player from Trustlist.")
    public Object[] removeTrustedPlayer(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.removeTrustedPlayer(args.checkString(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns maxiumum energy storage.")
    public Object[] getMaxEnergyStorage(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.storage.getMaxEnergyStored()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns current energy stored.")
    public Object[] getCurrentEnergyStorage(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getEnergyStored(ForgeDirection.UNKNOWN)};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently active.")
    public Object[] getActive(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.isActive()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; toggles turret redstone inversion state.")
    public Object[] setInverted(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        this.setInverted(args.checkBoolean(0));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        return null;
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; shows redstone inversion state.")
    public Object[] getInverted(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getInverted()};
    }

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; shows redstone state.")
    public Object[] getRedstone(Context context, Arguments args) {
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        return new Object[]{this.getRedstone()};
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public String getType() {
        // peripheral.getType returns whaaaaat?
        return "OMTBase";
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public String[] getMethodNames() {
        // list commands you want..
        return new String[]{commands.getOwner.toString(), commands.attacksPlayers.toString(),
                commands.setAttacksPlayers.toString(), commands.attacksMobs.toString(),
                commands.setAttacksMobs.toString(), commands.attacksNeutrals.toString(),
                commands.setAttacksNeutrals.toString(), commands.getTrustedPlayers.toString(),
                commands.addTrustedPlayer.toString(), commands.removeTrustedPlayer.toString(),
                commands.getActive.toString(), commands.getInverted.toString(),
                commands.getRedstone.toString(), commands.setInverted.toString(),
                commands.getType.toString()};
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        // method is command
        boolean b;
        int i;
        if (!computerAccessible) {
            return new Object[]{"Computer access deactivated!"};
        }
        switch (commands.values()[method]) {
            case getOwner:
                return new Object[]{this.getOwnerName()};
            case attacksPlayers:
                return new Object[]{this.attacksPlayers};
            case setAttacksPlayers:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksPlayers = b;
                return new Object[]{true};
            case attacksMobs:
                return new Object[]{this.attacksMobs};
            case setAttacksMobs:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksMobs = b;
                return new Object[]{true};
            case attacksNeutrals:
                return new Object[]{this.attacksNeutrals};
            case setAttacksNeutrals:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.attacksNeutrals = b;
                return new Object[]{true};
            case getTrustedPlayers:
                HashMap<String, Integer> result = new HashMap<>();
                if (this.getTrustedPlayers() != null && this.getTrustedPlayers().size() > 0) {
                    for (TrustedPlayer trustedPlayer : this.getTrustedPlayers()) {
                        result.put(trustedPlayer.name,
                                (trustedPlayer.canOpenGUI ? 1 : 0) + (trustedPlayer.canChangeTargeting ? 2 : 0) + (trustedPlayer.admin ? 4 : 0));
                    }
                }
                return new Object[]{result};
            case addTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                if (!this.addTrustedPlayer(arguments[0].toString())) {
                    return new Object[]{"Name not valid!"};
                }
                if (arguments[1].toString().equals("")) {
                    return new Object[]{"successfully added"};
                }
                for (i = 1; i <= 4; i++) {
                    if (arguments.length > i && !(arguments[i].toString().equals(
                            "true") || arguments[i].toString().equals("false"))) {
                        return new Object[]{"wrong arguments"};
                    }
                }
                TrustedPlayer trustedPlayer = this.getTrustedPlayer(arguments[0].toString());
                trustedPlayer.canOpenGUI = arguments[1].toString().equals("true");
                trustedPlayer.canChangeTargeting = arguments[2].toString().equals("true");
                trustedPlayer.admin = arguments[3].toString().equals("true");
                trustedPlayer.uuid = getPlayerUUID(arguments[0].toString());
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return new Object[]{"succesfully added player to trust list with parameters"};
            case removeTrustedPlayer:
                if (arguments[0].toString().equals("")) {
                    return new Object[]{"wrong arguments"};
                }
                this.removeTrustedPlayer(arguments[0].toString());
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return new Object[]{"removed player from trusted list"};
            case getActive:
                return new Object[]{this.active};
            case getInverted:
                return new Object[]{this.inverted};
            case getRedstone:
                return new Object[]{this.redstone};
            case setInverted:
                if (!(arguments[0].toString().equals("true") || arguments[0].toString().equals("false"))) {
                    return new Object[]{"wrong arguments"};
                }
                b = (arguments[0].toString().equals("true"));
                this.setInverted(b);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                return new Object[]{true};
            case getType:
                return new Object[]{this.getType()};
            default:
                break;
        }
        return new Object[]{false};
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public void attach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<>();
        }
        comp.add(computer);
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public void detach(IComputerAccess computer) {
        if (comp == null) {
            comp = new ArrayList<>();
        }
        comp.remove(computer);
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public boolean equals(IPeripheral other) {
        return other.getType().equals(getType());
    }

    public enum commands {
        getOwner, attacksPlayers, setAttacksPlayers, attacksMobs, setAttacksMobs, attacksNeutrals, setAttacksNeutrals,
        getTrustedPlayers, addTrustedPlayer, removeTrustedPlayer, getActive, getInverted, getRedstone, setInverted,
        getType
    }

    @Override
    public UCTileDefinition getTileDefinition() throws Exception {
        return new TurretBaseUCDefinition(this);
    }
}

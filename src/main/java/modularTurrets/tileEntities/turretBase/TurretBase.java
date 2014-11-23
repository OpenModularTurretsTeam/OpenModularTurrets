package modularTurrets.tileEntities.turretBase;

import java.awt.List;
import java.util.ArrayList;

import net.minecraft.dispenser.DispenserBehaviors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.network.PacketDispatcher;

public class TurretBase extends TileEntity implements IEnergyHandler,
	IInventory {

    public EnergyStorage storage;
    public ItemStack[] inv;
    public int yAxisDetect;
    public int baseTier;
    public boolean attacksMobs;
    public boolean attacksNeutrals;
    public boolean attacksPlayers;
    public String owner;
    public ArrayList trustedPlayers;
    public String splitter = "saisBlah";

    public TurretBase() {
	super();
    }

    public void addTrustedPlayer(String name) {
	trustedPlayers.add(name);
    }

    public void removeTrustedPlayer(String name) {
	trustedPlayers.remove(name);
    }

    public boolean isGettingRedstoneSignal() {
	if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
	    return true;
	}
	return false;
    }

    private String getStringedTrustedPlayers() {
	String sendBack = "";
	for (int i = 0; i < trustedPlayers.size(); i++) {
	    sendBack = sendBack + splitter + trustedPlayers.get(i);
	}
	return sendBack;
    }

    private ArrayList buildTrustedPlayersFromString(String trustedString) {

	String[] stringArray = trustedString.split(splitter);
	ArrayList newList = new ArrayList();
	for (int i = 0; i < stringArray.length; i++) {
	    if (!stringArray[i].equals(" ") || !stringArray[i].equals("")) {
		newList.add(stringArray[i]);
	    }
	}
	return newList;
    }

    public TurretBase(int MaxEnergyStorage, int MaxIO) {
	super();
	yAxisDetect = 2;
	this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
	attacksMobs = true;
	attacksNeutrals = true;
	attacksPlayers = false;
	trustedPlayers = new ArrayList();
    }

    @Override
    public void updateEntity() {
	if (!worldObj.isRemote) {
	    PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
	}
    }

    @Override
    public Packet getDescriptionPacket() {
	NBTTagCompound var1 = new NBTTagCompound();
	if (this.storage != null) {
	    var1.setInteger("energyStored",
		    this.getEnergyStored(ForgeDirection.UNKNOWN));
	    var1.setInteger("maxStorage", this.storage.getMaxEnergyStored());
	    var1.setInteger("maxIO", storage.getMaxReceive());
	}
	var1.setInteger("yAxisDetect", this.yAxisDetect);
	var1.setInteger("tier", baseTier);
	var1.setBoolean("attacksMobs", attacksMobs);
	var1.setBoolean("attacksNeutrals", attacksNeutrals);
	var1.setBoolean("attacksPlayers", attacksPlayers);
	var1.setString("owner", owner);
	var1.setString("trustedPlayers", getStringedTrustedPlayers());
	if (this.inv != null) {
	    NBTTagList itemList = new NBTTagList();
	    for (int i = 0; i < this.inv.length; i++) {
		ItemStack stack = this.inv[i];
		if (stack != null) {
		    NBTTagCompound tag = new NBTTagCompound();
		    tag.setByte("Slot", (byte) i);
		    stack.writeToNBT(tag);
		    itemList.appendTag(tag);
		}
	    }
	    var1.setTag("Inventory", itemList);
	}

	this.writeToNBT(var1);
	return new Packet132TileEntityData(this.xCoord, this.yCoord,
		this.zCoord, 2, var1);

    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
	super.writeToNBT(par1);
	if (this.storage != null) {
	    par1.setInteger("energyStored",
		    this.getEnergyStored(ForgeDirection.UNKNOWN));
	    par1.setInteger("maxStorage", this.storage.getMaxEnergyStored());
	    par1.setInteger("maxIO", this.storage.getMaxReceive());
	}
	par1.setInteger("yAxisDetect", this.yAxisDetect);
	par1.setInteger("tier", this.baseTier);
	par1.setBoolean("attacksMobs", attacksMobs);
	par1.setBoolean("attacksNeutrals", attacksNeutrals);
	par1.setBoolean("attacksPlayers", attacksPlayers);
	par1.setString("owner", owner);
	par1.setString("trustedPlayers", getStringedTrustedPlayers());
	if (this.inv != null) {
	    NBTTagList itemList = new NBTTagList();
	    for (int i = 0; i < this.inv.length; i++) {
		ItemStack stack = this.inv[i];
		if (stack != null) {
		    NBTTagCompound tag = new NBTTagCompound();
		    tag.setByte("Slot", (byte) i);
		    stack.writeToNBT(tag);
		    itemList.appendTag(tag);
		}
	    }
	    par1.setTag("Inventory", itemList);
	}
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
	super.readFromNBT(par1);
	if (this.storage != null) {
	    this.storage.setCapacity(par1.getInteger("maxStorage"));
	    this.storage.setEnergyStored(par1.getInteger("energyStored"));
	    this.storage.setMaxReceive(par1.getInteger("maxIO"));
	} else {
	    this.storage = new EnergyStorage(par1.getInteger("maxStorage"),
		    par1.getInteger("maxIO"));
	    this.storage.setEnergyStored(par1.getInteger("energyStored"));
	}
	this.yAxisDetect = par1.getInteger("yAxisDetect");
	this.baseTier = par1.getInteger("tier");
	this.attacksMobs = par1.getBoolean("attacksMobs");
	this.attacksNeutrals = par1.getBoolean("attacksNeutrals");
	this.attacksPlayers = par1.getBoolean("attacksPlayers");
	this.owner = par1.getString("owner");
	this.trustedPlayers = buildTrustedPlayersFromString(par1
		.getString("trustedPlayers"));
	if (this.inv != null) {
	    NBTTagList tagList = par1.getTagList("Inventory");
	    for (int i = 0; i < tagList.tagCount(); i++) {
		NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
		byte slot = tag.getByte("Slot");
		if (slot >= 0 && slot < inv.length) {
		    inv[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
	    }
	}
    }

    @Override
    public void onDataPacket(INetworkManager netManager,
	    Packet132TileEntityData packet) {
	readFromNBT(packet.data);
	this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public int getBaseTier() {
	return baseTier;
    }

    public void setBaseTier(int baseTier) {
	this.baseTier = baseTier;
    }

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

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive,
	    boolean simulate) {
	return storage.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract,
	    boolean simulate) {
	return 0;
    }

    @Override
    public boolean canInterface(ForgeDirection from) {
	return true;
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
	return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
	return storage.getMaxEnergyStored();
    }

    @Override
    public int getSizeInventory() {
	return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
	return inv[slot];
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
    public boolean isInvNameLocalized() {
	return false;
    }

    @Override
    public int getInventoryStackLimit() {
	return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
	return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this
		&& player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
			zCoord + 0.5) < 64;
    }

    @Override
    public void openChest() {

    }

    @Override
    public void closeChest() {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
	return true;
    }

    public EnergyStorage getStorage() {
	return storage;
    }

    public void setStorage(EnergyStorage storage) {
	this.storage = storage;
    }

    public ItemStack[] getInv() {
	return inv;
    }

    public void setInv(ItemStack[] inv) {
	this.inv = inv;
    }

    public int getyAxisDetect() {
	return yAxisDetect;
    }

    public void setyAxisDetect(int incoming) {
	this.yAxisDetect = this.yAxisDetect + incoming;

	if (this.yAxisDetect > 9) {
	    this.yAxisDetect = 9;
	}

	if (this.yAxisDetect < 0) {
	    this.yAxisDetect = 0;
	}
    }

    @Override
    public String getInvName() {
	return null;
    }

}

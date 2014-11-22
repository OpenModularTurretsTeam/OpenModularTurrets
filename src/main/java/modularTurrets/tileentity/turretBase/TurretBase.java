package modularTurrets.tileentity.turretBase;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraftforge.common.util.ForgeDirection;

public class TurretBase extends TileEntity implements IEnergyHandler, IInventory {

    public EnergyStorage storage;
    public ItemStack[] inv;
    public int yAxisDetect;
    public int baseTier;
    public boolean attacksMobs;
    public boolean attacksNeutrals;
    public boolean attacksPlayers;
    public String owner;
    public List<String> trustedPlayers;
    public String splitter = "saisBlah";

    public TurretBase() {
	    super();
    }

    public TurretBase(int MaxEnergyStorage, int MaxIO) {
        super();
        yAxisDetect = 2;
        this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
        attacksMobs = true;
        attacksNeutrals = true;
        attacksPlayers = false;
        trustedPlayers = new ArrayList<String>();
    }

    public void addTrustedPlayer(String name) {
        trustedPlayers.add(name);
    }

    public void removeTrustedPlayer(String name) {
	    trustedPlayers.remove(name);
    }

    public boolean isGettingRedstoneSignal() {
        return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    private String getStringedTrustedPlayers() {
        String sendBack = "";
        for (Object trustedPlayer : trustedPlayers) {
            sendBack = sendBack + splitter + trustedPlayer;
        }
        return sendBack;
    }

    private List<String> buildTrustedPlayersFromString(String trustedString) {

        String[] stringArray = trustedString.split(splitter);
        List<String> newList = new ArrayList<String>();

        for (String aStringArray : stringArray) {
            if (!aStringArray.equals(" ") || !aStringArray.equals("")) {
                newList.add(aStringArray);
            }
        }

        return newList;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        if (this.storage != null) {
            var1.setInteger("energyStored", this.getEnergyStored(ForgeDirection.UNKNOWN));
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

        FMLLog.info("getDescriptionPacket");

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, var1);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        if (this.storage != null) {
            par1.setInteger("energyStored", this.getEnergyStored(ForgeDirection.UNKNOWN));
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
            this.storage = new EnergyStorage(par1.getInteger("maxStorage"), par1.getInteger("maxIO"));
            this.storage.setEnergyStored(par1.getInteger("energyStored"));
        }

        this.yAxisDetect = par1.getInteger("yAxisDetect");
        this.baseTier = par1.getInteger("tier");
        this.attacksMobs = par1.getBoolean("attacksMobs");
        this.attacksNeutrals = par1.getBoolean("attacksNeutrals");
        this.attacksPlayers = par1.getBoolean("attacksPlayers");
        this.owner = par1.getString("owner");
        this.trustedPlayers = buildTrustedPlayersFromString(par1.getString("trustedPlayers"));

        if (this.inv != null) {
            NBTTagList tagList = par1.getTagList("Inventory", 0);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound tag = tagList.getCompoundTagAt(i);
                byte slot = tag.getByte("Slot");

                if (slot >= 0 && slot < inv.length) {
                    inv[slot] = ItemStack.loadItemStackFromNBT(tag);
                }
            }
        }
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
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
	    return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
                zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

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
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);

        readFromNBT(packet.func_148857_g());
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}

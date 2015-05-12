package openmodularturrets.tileentity.turretbase;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModularTurrets;
import openmodularturrets.network.EnergyStatusUpdateMessage;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import cpw.mods.fml.common.Optional;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public abstract class TurretBase extends TileEntity implements IEnergyHandler,
        IInventory, SimpleComponent {
    protected EnergyStorage storage;
    protected ItemStack[] inv;
    protected int yAxisDetect;
    protected boolean attacksMobs;
    protected boolean attacksNeutrals;
    protected boolean attacksPlayers;
    protected String owner;
    protected List<String> trustedPlayers;
    boolean newstate;

    public TurretBase(int MaxEnergyStorage, int MaxIO) {
        super();
        yAxisDetect = 2;
        this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
        attacksMobs = true;
        attacksNeutrals = true;
        attacksPlayers = false;
        trustedPlayers = new ArrayList<String>();
        this.inv = new ItemStack[this.getSizeInventory()];
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

    public List<String> getTrustedPlayers() {
        return trustedPlayers;
    }

    private NBTTagList getTrustedPlayersAsNBT() {
        NBTTagList nbt = new NBTTagList();

        for (String trustedPlayer : trustedPlayers) {
            nbt.appendTag(new NBTTagString(trustedPlayer));
        }

        return nbt;
    }

    private List<String> buildTrustedPlayersFromNBT(NBTTagList nbt) {
        List<String> trusted_players = new ArrayList<String>();

        for (int i = 0; i < nbt.tagCount(); i++) {
            trusted_players.add(nbt.getStringTagAt(i));
        }

        return trusted_players;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();

        this.writeToNBT(var1);

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
                this.zCoord, 2, var1);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);

        par1.setInteger("maxStorage", this.storage.getMaxEnergyStored());
        par1.setInteger("energyStored", this.getEnergyStored(ForgeDirection.UNKNOWN));
        par1.setInteger("maxIO", this.storage.getMaxReceive());
        par1.setInteger("yAxisDetect", this.yAxisDetect);
        par1.setBoolean("attacksMobs", attacksMobs);
        par1.setBoolean("attacksNeutrals", attacksNeutrals);
        par1.setBoolean("attacksPlayers", attacksPlayers);
        par1.setString("owner", owner);
        par1.setTag("trustedPlayers", getTrustedPlayersAsNBT());

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
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);

        this.storage.setCapacity(par1.getInteger("maxStorage"));
        this.storage.setEnergyStored(par1.getInteger("energyStored"));
        this.storage.setMaxReceive(par1.getInteger("maxIO"));

        this.yAxisDetect = par1.getInteger("yAxisDetect");
        this.attacksMobs = par1.getBoolean("attacksMobs");
        this.attacksNeutrals = par1.getBoolean("attacksNeutrals");
        this.attacksPlayers = par1.getBoolean("attacksPlayers");
        this.owner = par1.getString("owner");
        this.trustedPlayers = buildTrustedPlayersFromNBT(par1.getTagList("trustedPlayers", 8));

        NBTTagList tagList = par1.getTagList("Inventory", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inv.length) {
                inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
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
    public void onDataPacket(NetworkManager net,
                             S35PacketUpdateTileEntity packet) {
        super.onDataPacket(net, packet);

        readFromNBT(packet.func_148857_g());
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (this.worldObj.isRemote) {
            return;
        }

        EnergyStatusUpdateMessage message = new EnergyStatusUpdateMessage(
                this.xCoord, this.yCoord, this.zCoord,
                this.getEnergyStored(ForgeDirection.UNKNOWN));

        ModularTurrets.networking.sendToAll(message);
    }
    
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():string; returns owner of turret base.")
	public Object[] getOwner(Context context, Arguments args) {
		return new Object[] { this.getOwner() };
	}
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack hostile mobs.")
	public Object[] isAttacksMobs(Context context, Arguments args) {
		return new Object[] { this.isAttacksMobs() };
	}
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack neutral mobs.")
	public Object[] isAttacksNeutrals(Context context, Arguments args) {
		return new Object[] { this.isAttacksNeutrals() };
	}
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():boolean; returns if the turret is currently set to attack players.")
	public Object[] isAttacksPlayers(Context context, Arguments args) {
		return new Object[] { this.isAttacksPlayers() };
	}
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():table; returns a table of trusted players on this base.")
	public Object[] getTrustedPlayers(Context context, Arguments args) {
		return new Object[] { this.getTrustedPlayers() };
	}
    
    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns maxiumum energy storage.")
	public Object[] getMaxEnergyStorage(Context context, Arguments args) {
		return new Object[] { this.storage.getMaxEnergyStored() };
	}

    @Optional.Method(modid = "OpenComputers")
    @Callback(doc = "function():int; returns current energy stored.")
	public Object[] getCurrentEnergyStorage(Context context, Arguments args) {
		return new Object[] { this.getEnergyStored(ForgeDirection.UNKNOWN) };
		
	}

}

package openmodularturrets.tileentity.turretbase;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModularTurrets;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public abstract class TurretBase extends TileEntity implements IEnergyHandler,
		IInventory, SimpleComponent, ISidedInventory {
	protected EnergyStorage storage;
	protected ItemStack[] inv;
	protected int yAxisDetect;
	protected boolean attacksMobs;
	protected boolean attacksNeutrals;
	protected boolean attacksPlayers;
	protected String owner;
	protected List<TrustedPlayer> trustedPlayers;
	protected int ticks;
	public int currentTrustedPlayerAdmin = 0;

	public TurretBase(int MaxEnergyStorage, int MaxIO) {
		super();
		yAxisDetect = 2;
		this.storage = new EnergyStorage(MaxEnergyStorage, MaxIO);
		attacksMobs = true;
		attacksNeutrals = true;
		attacksPlayers = false;
		trustedPlayers = new ArrayList<TrustedPlayer>();
		this.inv = new ItemStack[this.getSizeInventory()];
	}

	public void addTrustedPlayer(String name) {
		trustedPlayers.add(new TrustedPlayer(name));
	}

	public void removeTrustedPlayer(String name) {
    	List<TrustedPlayer> copiedTrusteds = new ArrayList();
    	copiedTrusteds.addAll(trustedPlayers);
    	for(int i =0;i<=copiedTrusteds.size()-1;i++)
    	{
    		TrustedPlayer player = copiedTrusteds.get(i);
    		if(player.getName().equals(name))
    		{
    			trustedPlayers.remove(i);
    		}
    	}
    }

	public boolean isGettingRedstoneSignal() {
		return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
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

	private NBTTagList getTrustedPlayersAsNBT() {
		NBTTagList nbt = new NBTTagList();
		for (TrustedPlayer trustedPlayer : trustedPlayers) {
			NBTTagCompound nbtPlayer = new NBTTagCompound();
			nbtPlayer.setString("name", trustedPlayer.name);
			nbtPlayer.setBoolean("canOpenGUI", trustedPlayer.canOpenGUI);
			nbtPlayer.setBoolean("canChangeTargeting",
					trustedPlayer.canChangeTargeting);
			nbtPlayer.setBoolean("canAddTrustedPlayers",
					trustedPlayer.canAddTrustedPlayers);
			nbtPlayer.setBoolean("isAdmin", trustedPlayer.isAdmin);
			nbt.appendTag(nbtPlayer);
		}
		return nbt;
	}

	private void buildTrustedPlayersFromNBT(NBTTagList nbt) {
		trustedPlayers.clear();
		for (int i = 0; i < nbt.tagCount(); i++) {
			if (!nbt.getCompoundTagAt(i).getString("name").equals("")) {
				NBTTagCompound nbtPlayer = nbt.getCompoundTagAt(i);
				TrustedPlayer trustedPlayer = new TrustedPlayer(
						nbtPlayer.getString("name"));
				trustedPlayer.canOpenGUI = nbtPlayer.getBoolean("canOpenGUI");
				trustedPlayer.canChangeTargeting = nbtPlayer
						.getBoolean("canChangeTargeting");
				trustedPlayer.canAddTrustedPlayers = nbtPlayer
						.getBoolean("canAddTrustedPlayers");
				trustedPlayer.isAdmin = nbtPlayer.getBoolean("isAdmin");
				trustedPlayers.add(trustedPlayer);
			} else if (nbt.getCompoundTagAt(i).getString("name").equals("")) {
				TrustedPlayer trustedPlayer = new TrustedPlayer(
						nbt.getStringTagAt(i));
				Logger.getGlobal()
						.info("found legacy trusted Player: "
								+ nbt.getStringTagAt(i));
				trustedPlayers.add(trustedPlayer);
			}
		}
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
		par1.setInteger("energyStored",
				this.getEnergyStored(ForgeDirection.UNKNOWN));
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

		buildTrustedPlayersFromNBT(par1.getTagList("trustedPlayers", 10)); // not
																			// sure
																			// if
																			// we
																			// need
																			// both
																			// -
																			// Keridos
		// buildTrustedPlayersFromNBT(par1.getTagList("trustedPlayers", 8));

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
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.worldObj.isRemote) {
			return;
		}

		ticks++;

		if (ticks % 5 == 0) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ticks = 0;
		}
	}
	
	 @Override
	    public int[] getAccessibleSlotsFromSide(int side) {
	        return new int[]{0,1,2,3,4,5,6,7,8};
	    }

	    @Override
	    public boolean canInsertItem(int slotID, ItemStack itemstack, int side)
	    {
	        return isItemValidForSlot(slotID, itemstack);
	    }

	    @Override
	    public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	    {
	        return true;
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
	@Callback(doc = "function():boolean;  sets to attack hostile mobs or not.")
	public Object[] setAttacksMobs(Context context, Arguments args) {
		this.setAttacksMobs(args.checkBoolean(0));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return null;
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():boolean; returns if the turret is currently set to attack neutral mobs.")
	public Object[] isAttacksNeutrals(Context context, Arguments args) {
		return new Object[] { this.isAttacksNeutrals() };
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():boolean; sets to attack neutral mobs or not.")
	public Object[] setAttacksNeutrals(Context context, Arguments args) {
		this.setAttacksNeutrals(args.checkBoolean(0));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return null;
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():boolean; returns if the turret is currently set to attack players.")
	public Object[] isAttacksPlayers(Context context, Arguments args) {
		return new Object[] { this.isAttacksPlayers() };
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():boolean; sets to attack players or not.")
	public Object[] setAttacksPlayers(Context context, Arguments args) {
		this.setAttacksPlayers(args.checkBoolean(0));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return null;
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():table; returns a table of trusted players on this base.")
	public Object[] getTrustedPlayers(Context context, Arguments args) {
		return new Object[] { this.getTrustedPlayers() };
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function(name:String, [canOpenGUI:boolean , canChangeTargeting:boolean , "
			+ "canAddTrustedPlayers:boolean , isAdmin:boolean]):string; adds Trusted player to Trustlist.")
	public Object[] addTrustedPlayer(Context context, Arguments args) {
		this.addTrustedPlayer(args.checkString(0));
		TrustedPlayer trustedPlayer = this
				.getTrustedPlayer(args.checkString(0));
		trustedPlayer.canOpenGUI = args.optBoolean(1, false);
		trustedPlayer.canChangeTargeting = args.optBoolean(1, false);
		trustedPlayer.canAddTrustedPlayers = args.optBoolean(1, false);
		trustedPlayer.isAdmin = args.optBoolean(1, false);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return null;
	}

	@Optional.Method(modid = "OpenComputers")
	@Callback(doc = "function():string; removes Trusted player from Trustlist.")
	public Object[] removeTrustedPlayer(Context context, Arguments args) {
		this.removeTrustedPlayer(args.checkString(0));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return null;
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

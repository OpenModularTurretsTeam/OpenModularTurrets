package openmodularturrets.tileentity.expander;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.TurretHeadUtil;

public abstract class AbstractInvExpander extends TileEntity implements IInventory {
    public float baseFitRotationX;
    public float baseFitRotationZ;
    public TurretBase base;
    public boolean hasSetSide = false;
    protected ItemStack[] inv;
    public int tier;

    public AbstractInvExpander(int size) {
        this.inv = new ItemStack[this.getSizeInventory()];
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
                this.zCoord, 2, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound var1 = pkt.func_148857_g();
        readFromNBT(var1);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);


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


        NBTTagList tagList = par1.getTagList("Inventory", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = tagList.getCompoundTagAt(i);
            byte slot = tag.getByte("Slot");

            if (slot >= 0 && slot < inv.length) {
                inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    public void setSide() {
        if (hasSetSide) {
            return;
        }

        if (worldObj.getTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
        }
    }

    @Override
    public void updateEntity() {

        if (!hasSetSide) {
            setSide();
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
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
                zCoord + 0.5) < 64;
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
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, xCoord, yCoord, zCoord);
    }
}

package openmodularturrets.tileentity;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import openmodularturrets.util.TurretHeadUtil;

import static openmodularturrets.util.MathUtil.truncateDoubleToInt;

public class Expander extends TileEntityContainer implements ITickable {
    public float baseFitRotationX;
    public float baseFitRotationZ;
    protected TurretBase base;
    private boolean hasSetSide = false;
    private boolean powerExpander;
    private EnumFacing orientation;

    public Expander() {
        this.inv = new ItemStack[9];
        this.orientation = EnumFacing.NORTH;
    }

    public Expander(int tier, boolean powerExpander) {
        this.inv = new ItemStack[9];
        this.tier = tier;
        this.powerExpander = powerExpander;
        this.orientation = EnumFacing.NORTH;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("powerExpander", powerExpander);
        nbtTagCompound.setByte("direction", (byte) orientation.ordinal());
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.powerExpander= nbtTagCompound.getBoolean("powerExpander");
        if (nbtTagCompound.hasKey("direction")) {
            this.orientation = EnumFacing.getFront(nbtTagCompound.getByte("direction"));
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return truncateDoubleToInt(Math.pow(2,tier+1));
    }

    private void setSide() {
        if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.orientation = EnumFacing.EAST;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.orientation = EnumFacing.WEST;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.orientation = EnumFacing.DOWN;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.orientation = EnumFacing.UP;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.orientation = EnumFacing.NORTH;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.orientation = EnumFacing.SOUTH;
            this.hasSetSide = true;
        }
        this.markDirty();
    }

    @Override
    public void update() {
        if (!hasSetSide) {
            setSide();
        }

        if (getBase() == null) {
            this.getWorld().destroyBlock(this.pos, true);
        }
    }


    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return !isPowerExpander();
    }

    public int getTier() {
        return tier;
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, this.pos);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0,1,2,3,4,5,6,7,8};
    }

    public boolean isPowerExpander() {
        return powerExpander;
    }

    public EnumFacing getOrientation() {
        return orientation;
    }

    public void setOrientation(EnumFacing orientation) {
        this.orientation = orientation;
    }
}

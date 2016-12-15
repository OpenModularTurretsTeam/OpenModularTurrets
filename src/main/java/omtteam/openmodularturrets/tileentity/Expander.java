package omtteam.openmodularturrets.tileentity;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import omtteam.omlib.tileentity.TileEntityContainer;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.MathUtil.truncateDoubleToInt;

public class Expander extends TileEntityContainer implements ITickable {
    @SuppressWarnings("unused")
    protected TurretBase base;
    private boolean powerExpander;
    private EnumFacing orientation;
    protected int tier;

    public Expander() {
        this.inventory = new ItemStack[9];
        this.orientation = EnumFacing.NORTH;
    }

    public Expander(int tier, boolean powerExpander) {
        this.inventory = new ItemStack[9];
        this.tier = tier;
        this.powerExpander = powerExpander;
        this.orientation = EnumFacing.NORTH;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setBoolean("powerExpander", powerExpander);
        nbtTagCompound.setByte("direction", (byte) orientation.ordinal());
        nbtTagCompound.setInteger("tier", tier);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.powerExpander= nbtTagCompound.getBoolean("powerExpander");
        this.tier = nbtTagCompound.getInteger("tier");
        if (nbtTagCompound.hasKey("direction")) {
            this.setOrientation(EnumFacing.getFront(nbtTagCompound.getByte("direction")));
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return truncateDoubleToInt(Math.pow(2,tier+1));
    }

    public void setSide() {
        if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.EAST);
            return;
        }

        if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.WEST);
            return;
        }

        if (worldObj.getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.DOWN);
            return;
        }

        if (worldObj.getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.UP);
            return;
        }

        if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.NORTH);
            return;
        }

        if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.setOrientation(EnumFacing.SOUTH);
        }
    }

    @Override
    public void update() {
        if (worldObj.getWorldTime() % 15 == 0 && getBase() == null || dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
        }
    }


    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return !isPowerExpander();
    }

    public int getTier() {
        return tier;
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, this.pos);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    @ParametersAreNonnullByDefault
    @Nonnull
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[]{0,1,2,3,4,5,6,7,8};
    }

    public boolean isPowerExpander() {
        return powerExpander;
    }

    public EnumFacing getOrientation() {
        return orientation;
    }

    private void setOrientation(EnumFacing orientation) {
        this.orientation = orientation;
    }
}

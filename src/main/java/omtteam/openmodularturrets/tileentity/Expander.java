package omtteam.openmodularturrets.tileentity;


import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import omtteam.omlib.tileentity.TileEntityContainer;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.compat.ItemStackList;
import omtteam.openmodularturrets.api.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.MathUtil.truncateDoubleToInt;
import static omtteam.openmodularturrets.util.TurretHeadUtil.getTurretBaseFacing;

public class Expander extends TileEntityContainer implements ITickable, ITurretBaseAddonTileEntity {
    @SuppressWarnings("unused")
    protected TurretBase base;
    private boolean powerExpander;
    private EnumFacing orientation;
    private int tier;

    public Expander() {
        this.inventory = ItemStackList.create(9);
        this.orientation = EnumFacing.NORTH;
    }

    public Expander(int tier, boolean powerExpander) {
        this.inventory = ItemStackList.create(9);
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
        this.powerExpander = nbtTagCompound.getBoolean("powerExpander");
        this.tier = nbtTagCompound.getInteger("tier");
        if (nbtTagCompound.hasKey("direction")) {
            this.setOrientation(EnumFacing.getFront(nbtTagCompound.getByte("direction")));
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return truncateDoubleToInt(Math.pow(2, tier + 1));
    }

    public void setSide() {
        this.setOrientation(getTurretBaseFacing(this.getWorld(), this.pos));
    }

    @Override
    public void update() {
        if (this.getWorld().getWorldTime() % 15 == 0 && getBase() == null || dropBlock) {
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
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
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
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
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

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return base;
    }
}

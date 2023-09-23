package omtteam.openmodularturrets.tileentity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import omtteam.omlib.tileentity.TileEntityContainer;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nonnull;

import static omtteam.openmodularturrets.turret.TurretHeadUtil.getTurretBaseFacing;

public class PowerGenerator extends TileEntityContainer implements ITickable, ITurretBaseAddonTileEntity {
    protected IItemHandlerModifiable inventory;
    private EnumFacing orientation;

    private PowerGenState state;

    public PowerGenerator() {
        super();
        setupInventory();
        this.orientation = EnumFacing.NORTH;
    }

    protected void setupInventory() {
        inventory = new ItemStackHandler(7) {

            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                markDirty();
            }

            @SuppressWarnings("BooleanMethodIsAlwaysInverted")
            public boolean isItemValidForSlot(ItemStack stack) {
                return stack.getItem().getItemBurnTime(stack) >= 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValidForSlot(stack) || stack.getItem() != Items.REDSTONE && slot < 6)
                    return stack;
                else if (stack.getItem() == Items.REDSTONE && slot == 6)
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }
        };
    }

    @Override
    public RangedWrapper getCapabilityInventory(EnumFacing facing) {
        return new RangedWrapper(inventory, 0, 9);
    }

    @Nonnull
    @Override
    public NBTTagCompound saveToNBT(NBTTagCompound nbtTagCompound) {
        super.saveToNBT(nbtTagCompound);
        nbtTagCompound.setByte("direction", (byte) orientation.ordinal());
        return nbtTagCompound;
    }

    @Override
    public void loadFromNBT(NBTTagCompound nbtTagCompound) {
        super.loadFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey("direction")) {
            this.setOrientation(EnumFacing.getFront(nbtTagCompound.getByte("direction")));
        }
    }

    public void setSide() {
        this.setOrientation(getTurretBaseFacing(this.getWorld(), this.pos));
    }

    @Override
    public void update() {
        if (this.getWorld().getWorldTime() % 15 == 0 && dropBlock) {
            this.getWorld().destroyBlock(this.pos, true);
        }
    }

    public TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
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
        return getBase();
    }

    @Override
    public IItemHandlerModifiable getInventory() {
        return inventory;
    }

    public PowerGenState getState() {
        return state;
    }

    public enum PowerGenState {
        off, on, boosted
    }
}

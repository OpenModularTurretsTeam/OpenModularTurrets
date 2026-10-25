package omtteam.openmodularturrets.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import omtteam.omlib.container.WindowValues;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Generator container (1.20.1+ {@code GeneratorMenu}): the player inventory, a 3×3 grid of fuel slots and the upgrade
 * slot. Energy, capacity and burn progress are synced as window properties (OMLib's {@link WindowValues}).
 */
public class GeneratorContainer extends Container {
    private static final int DATA_ENERGY = 0;
    private static final int DATA_CAPACITY = 1;
    private static final int DATA_BURN_TIME = 2;
    private static final int DATA_BURN_DURATION = 3;
    /** Index of the first fuel slot (after the 36 player inventory slots). */
    private static final int FUEL_START = 36;
    private static final int FUEL_END = FUEL_START + GeneratorTileEntity.FUEL_SLOTS;
    private static final int UPGRADE_SLOT = FUEL_END;
    /** Position of the top left fuel slot's item; the grid has 3 columns. */
    public static final int FUEL_X = 8;
    public static final int FUEL_Y = 17;
    public static final int FUEL_COLUMNS = 3;
    /** Position of the upgrade slot's item: centred in the free area, level with the middle fuel row. */
    public static final int UPGRADE_X = 90;
    public static final int UPGRADE_Y = 35;

    private final GeneratorTileEntity generator;
    private final WindowValues values;

    public GeneratorContainer(InventoryPlayer inventoryPlayer, GeneratorTileEntity generator) {
        this.generator = generator;
        this.values = new WindowValues(() -> (int) Math.min(Integer.MAX_VALUE, generator.getEnergy()),
                () -> (int) Math.min(Integer.MAX_VALUE, generator.getCapacity()), generator::getBurnTime,
                generator::getBurnDuration);
        for (int x = 0; x < 9; x++) {
            addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inventoryPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        for (int slot = 0; slot < GeneratorTileEntity.FUEL_SLOTS; slot++) {
            addSlotToContainer(new SlotItemHandler(generator.getFuel(), slot, FUEL_X + slot % FUEL_COLUMNS * 18,
                    FUEL_Y + slot / FUEL_COLUMNS * 18));
        }
        addSlotToContainer(new SlotItemHandler(generator.getUpgrades(), 0, UPGRADE_X, UPGRADE_Y));
    }

    public long getEnergy() {
        return values.get(DATA_ENERGY);
    }

    public long getCapacity() {
        return values.get(DATA_CAPACITY);
    }

    /** Remaining burn time of the current fuel as a fraction (0 = not burning). */
    public float getBurnProgress() {
        int duration = values.get(DATA_BURN_DURATION);
        return duration <= 0 ? 0F : Math.min(1F, (float) values.get(DATA_BURN_TIME) / duration);
    }

    @Override
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);
        values.addListener(this, listener);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        values.detectAndSendChanges(this, listeners);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        values.updateProgressBar(id, data);
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(EntityPlayer player) {
        return !generator.isInvalid() && player.getDistanceSq(generator.getPos().getX() + 0.5,
                generator.getPos().getY() + 0.5, generator.getPos().getZ() + 0.5) < 64;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getStack();
        ItemStack original = stack.copy();
        if (index < FUEL_START) {
            if (GeneratorTileEntity.isUpgrade(stack)) {
                if (!mergeItemStack(stack, UPGRADE_SLOT, UPGRADE_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!GeneratorTileEntity.isFuel(stack) || !mergeItemStack(stack, FUEL_START, FUEL_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!mergeItemStack(stack, 0, FUEL_START, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.putStack(ItemStack.EMPTY);
        } else {
            slot.onSlotChanged();
        }
        return stack.getCount() == original.getCount() ? ItemStack.EMPTY : original;
    }
}

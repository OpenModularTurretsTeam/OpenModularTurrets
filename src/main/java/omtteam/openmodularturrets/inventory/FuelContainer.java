package omtteam.openmodularturrets.inventory;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import omtteam.omlib.inventory.LargeStackContainer;
import omtteam.openmodularturrets.core.storage.SlotCapacity;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The generator's fuel slots: each holds one stack plus the stacks of the attached inventory expanders
 * ({@link SlotCapacity}), except for fuel that leaves a remainder (a lava bucket): the generator puts the remainder
 * into the emptied slot, so a slot of several such items would lose all but the last one's. The generator menu's
 * client side uses it too, with the capacity the server syncs.
 */
public class FuelContainer extends SimpleContainer implements LargeStackContainer {
    /** Max stack size of most items, the limit {@link #getSlotLimit} gives for an empty stack. */
    private static final int DEFAULT_STACK = 64;

    private final Predicate<ItemStack> isFuel;
    private final Predicate<ItemStack> leavesRemainder;
    private final Supplier<SlotCapacity> capacity;

    /**
     * @param isFuel          what automation may insert
     * @param leavesRemainder fuel that keeps its normal limit (asked for non-empty stacks only)
     * @param capacity        what the attached expanders add, asked on every limit check
     */
    public FuelContainer(int size, Predicate<ItemStack> isFuel, Predicate<ItemStack> leavesRemainder,
                         Supplier<SlotCapacity> capacity) {
        super(size);
        this.isFuel = isFuel;
        this.leavesRemainder = leavesRemainder;
        this.capacity = capacity;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return isFuel.test(stack);
    }

    @Override
    public int getSlotLimit(int slot, ItemStack stack) {
        if (stack.isEmpty()) return capacity.get().limit(DEFAULT_STACK);
        return leavesRemainder.test(stack) ? stack.getMaxStackSize() : capacity.get().limit(stack.getMaxStackSize());
    }
}

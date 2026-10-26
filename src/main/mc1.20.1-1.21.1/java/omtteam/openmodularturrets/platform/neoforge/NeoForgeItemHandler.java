package omtteam.openmodularturrets.platform.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.RangedWrapper;
import omtteam.openmodularturrets.inventory.ItemAccess;

/**
 * NeoForge view ({@code IItemHandler}) of an {@link ItemAccess}: NeoForge's container wrapper, limited to the slot range
 * and filtered.
 */
public final class NeoForgeItemHandler extends RangedWrapper {
    private final ItemAccess access;

    public NeoForgeItemHandler(ItemAccess access) {
        super(new InvWrapper(access.container()), access.firstSlot(), access.firstSlot() + access.slotCount());
        this.access = access;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return access.canInsert().test(stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack inSlot = getStackInSlot(slot);
        return !inSlot.isEmpty() && access.canExtract().test(inSlot) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return access.canInsert().test(stack) && super.isItemValid(slot, stack);
    }
}

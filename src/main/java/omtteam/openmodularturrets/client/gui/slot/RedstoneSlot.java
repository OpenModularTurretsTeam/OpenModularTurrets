package omtteam.openmodularturrets.client.gui.slot;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class RedstoneSlot extends SlotItemHandler {
    @SuppressWarnings("SameParameterValue")
    public RedstoneSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return itemStack.getItem() == Items.REDSTONE;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return Math.min(64, this.getSlotStackLimit());
    }
}

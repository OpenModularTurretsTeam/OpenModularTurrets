package omtteam.openmodularturrets.client.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import omtteam.openmodularturrets.items.UpgradeMetaItem;

import javax.annotation.Nonnull;

public class UpgradeSlot extends SlotItemHandler {
    @SuppressWarnings("SameParameterValue")
    public UpgradeSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof UpgradeMetaItem;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return (4);
    }

    @Override
    public int getSlotStackLimit() {
        return 4;
    }
}

package omtteam.openmodularturrets.client.gui.customSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.items.UpgradeMetaItem;


public class UpgradeSlot extends Slot {
    @SuppressWarnings("SameParameterValue")
    public UpgradeSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof UpgradeMetaItem;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return (4);
    }

    @Override
    public int getSlotStackLimit() {
        return 4;
    }
}

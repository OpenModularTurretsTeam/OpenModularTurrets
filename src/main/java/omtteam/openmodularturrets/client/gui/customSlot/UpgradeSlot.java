package omtteam.openmodularturrets.client.gui.customSlot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import omtteam.openmodularturrets.items.UpgradeMetaItem;


public class UpgradeSlot extends SlotItemHandler {
    @SuppressWarnings("SameParameterValue")
    public UpgradeSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
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

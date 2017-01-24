package omtteam.openmodularturrets.client.gui.customSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.items.AmmoMetaItem;

public class AmmoSlot extends Slot {
    @SuppressWarnings("SameParameterValue")
    public AmmoSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack par1ItemStack) {
        for (ItemStack itemStack: ConfigHandler.disposableAmmoList) {
            if (itemStack.getItem() == par1ItemStack.getItem() && (itemStack.stackSize == 2 ||
                    itemStack.getItemDamage() == par1ItemStack.getItemDamage())) {
                return true;
            }
        }
        return par1ItemStack.getItem() instanceof AmmoMetaItem;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 64;
    }
}

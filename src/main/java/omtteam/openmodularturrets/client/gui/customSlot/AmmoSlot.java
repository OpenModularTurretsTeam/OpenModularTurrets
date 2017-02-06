package omtteam.openmodularturrets.client.gui.customSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static omtteam.openmodularturrets.util.OMTUtil.isItemStackValidAmmo;

public class AmmoSlot extends Slot {
    @SuppressWarnings("SameParameterValue")
    public AmmoSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return isItemStackValidAmmo(itemStack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 64;
    }
}

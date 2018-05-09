package omtteam.openmodularturrets.client.gui.customSlot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import static omtteam.openmodularturrets.util.OMTUtil.isItemStackValidAmmo;

public class AmmoSlot extends SlotItemHandler {
    @SuppressWarnings("SameParameterValue")
    public AmmoSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return isItemStackValidAmmo(itemStack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return Math.min(64, this.getSlotStackLimit());
    }
}

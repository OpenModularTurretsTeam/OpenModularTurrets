package omtteam.openmodularturrets.client.gui.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import omtteam.openmodularturrets.items.AddonMetaItem;

import javax.annotation.Nonnull;

public class AddonSlot extends SlotItemHandler {
    @SuppressWarnings("SameParameterValue")
    public AddonSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return par1ItemStack.getItem() instanceof AddonMetaItem;
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack) {
        return (stack.getItemDamage() == 1 || stack.getItemDamage() == 7 ? 4 : 1);
    }
}

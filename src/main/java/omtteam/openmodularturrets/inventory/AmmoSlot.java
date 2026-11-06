package omtteam.openmodularturrets.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import omtteam.omlib.container.LargeStackSlot;

import javax.annotation.Nonnull;

import static omtteam.openmodularturrets.inventory.AmmoRules.isItemStackValidAmmo;

/** An ammo slot of a turret base: ammo only, as many stacks as the base tier and its expanders allow. */
public class AmmoSlot extends LargeStackSlot {
    @SuppressWarnings("SameParameterValue")
    public AmmoSlot(IItemHandler inventory, int par2, int par3, int par4) {
        super(inventory, par2, par3, par4);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return isItemStackValidAmmo(itemStack);
    }
}

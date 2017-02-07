package omtteam.openmodularturrets.util;

import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.items.AmmoMetaItem;

import static omtteam.omlib.util.compat.ItemStackTools.getStackSize;

/**
 * Created by Keridos on 06/02/17.
 * This Class
 */
public class OMTUtil {
    public static boolean isItemStackValidAmmo(ItemStack itemStack) {
        if (!ConfigHandler.useWhitelistForAmmo) return true;
        for (ItemStack itemStackAllowed : ConfigHandler.disposableAmmoList) {
            if (itemStackAllowed.getItem() == itemStack.getItem() && (getStackSize(itemStack) == 2 ||
                    itemStackAllowed.getItemDamage() == itemStack.getItemDamage())) {
                return true;
            }
        }
        return itemStack.getItem() instanceof AmmoMetaItem;
    }
}

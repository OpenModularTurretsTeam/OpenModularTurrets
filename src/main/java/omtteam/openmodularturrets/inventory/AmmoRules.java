package omtteam.openmodularturrets.inventory;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.turret.TurretRegistry;

/** Which items the ammo slots of a base accept. */
public class AmmoRules {
    public static boolean isItemStackValidAmmo(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) return false;
        return !OMTConfig.GENERAL.useWhitelistForAmmo || itemStack.getItem() == Items.POTATO
                || itemStack.getItem() == Items.REDSTONE || itemStack.getItem() == Items.ARROW
                || itemStack.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_BLOCK)
                || TurretRegistry.isAnyAmmo(itemStack);
    }
}

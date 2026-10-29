package omtteam.openmodularturrets.item;

import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.UpgradeType;
import omtteam.openmodularturrets.tileentity.base.ItemClassifier;

/**
 * The config switches of the upgrades and addons ({@code Upgrades}, {@code Addons}), as the config in effect has them
 * (the server's on a client, OMLib's {@code ConfigSync}). A disabled upgrade or addon has no recipe and does nothing in
 * a machine; the client marks it.
 */
public final class ModuleToggles {
    private ModuleToggles() {
    }

    public static boolean isEnabled(UpgradeType type) {
        return OMTConfig.UPGRADES.isEnabled(type);
    }

    public static boolean isEnabled(AddonType type) {
        return OMTConfig.ADDONS.isEnabled(type);
    }

    /** Whether the stack is an upgrade or addon that is disabled in the config. */
    public static boolean isDisabled(ItemStack stack) {
        if (stack.isEmpty()) return false;
        if (stack.getItem() instanceof ItemGeneratorUpgrade) {
            return !isEnabled(((ItemGeneratorUpgrade) stack.getItem()).getUpgradeType());
        }
        return ItemClassifier.getUpgrade(stack).map(type -> !isEnabled(type)).orElse(false)
                || ItemClassifier.getAddon(stack).map(type -> !isEnabled(type)).orElse(false);
    }
}

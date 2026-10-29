package omtteam.openmodularturrets.item;

import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import omtteam.openmodularturrets.core.item.UpgradeType;

/**
 * The config switches of the upgrades and addons ({@code upgrades.<name>}, {@code addons.<name>}), as the config in
 * effect has them (the server's on a client). A disabled upgrade or addon has no recipe and does nothing in a machine;
 * the client marks it.
 */
public final class ModuleToggles {
    private static final String UPGRADE_PREFIX = "upgrade_";
    private static final String ADDON_PREFIX = "addon_";

    private ModuleToggles() {
    }

    public static boolean isEnabled(UpgradeType type) {
        return OMTConfig.get().upgrades.isEnabled(type);
    }

    public static boolean isEnabled(AddonType type) {
        return OMTConfig.get().addons.isEnabled(type);
    }

    /** Whether the stack is an upgrade or addon that is disabled in the config. */
    public static boolean isDisabled(ItemStack stack) {
        if (stack.getItem() instanceof UpgradeItem upgrade) return !isEnabled(upgrade.getUpgradeType());
        if (stack.getItem() instanceof AddonItem addon) return !isEnabled(addon.getAddonType());
        return false;
    }

    /**
     * Whether the upgrade or addon with this item id path ({@code upgrade_<name>}, {@code addon_<name>}) is enabled;
     * true for any other path. Used by the recipe condition, which runs before the items may be looked up.
     */
    public static boolean isItemEnabled(String path) {
        if (path.startsWith(UPGRADE_PREFIX)) {
            String name = path.substring(UPGRADE_PREFIX.length());
            for (TurretBaseUpgradeType type : TurretBaseUpgradeType.values()) {
                if (type.getSerializedName().equals(name)) return isEnabled(type);
            }
            for (GeneratorUpgradeType type : GeneratorUpgradeType.values()) {
                if (type.getSerializedName().equals(name)) return isEnabled(type);
            }
        } else if (path.startsWith(ADDON_PREFIX)) {
            String name = path.substring(ADDON_PREFIX.length());
            for (AddonType type : AddonType.values()) {
                if (type.getSerializedName().equals(name)) return isEnabled(type);
            }
        }
        return true;
    }
}

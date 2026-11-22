package omtteam.openmodularturrets.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Recipe condition {@code openmodularturrets:module_enabled} (as on 1.20.1+): {@code "items"} lists item names of the
 * 1.20.1+ form ({@code upgrade_<name>}, {@code addon_<name>}), and the recipe loads only if one of them is enabled in
 * the config ({@link OMTConfig#UPGRADES}, {@link OMTConfig#ADDONS}). Recipes follow the config when they load (restart).
 */
@SuppressWarnings("unused")
public class ModuleEnabledConditionFactory implements IConditionFactory {
    private static final String UPGRADE_PREFIX = "upgrade_";
    private static final String ADDON_PREFIX = "addon_";

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json) {
        JsonArray array = JsonUtils.getJsonArray(json, "items");
        List<String> items = new ArrayList<>();
        for (JsonElement element : array) {
            items.add(JsonUtils.getString(element, "items"));
        }
        return () -> anyEnabled(items);
    }

    /** Whether one of the named upgrades or addons is enabled. */
    public static boolean anyEnabled(List<String> items) {
        for (String item : items) {
            if (isItemEnabled(item)) return true;
        }
        return false;
    }

    /**
     * Whether the upgrade or addon with this name ({@code upgrade_<name>}, {@code addon_<name>}, the serialized names
     * of the core types) is enabled; true for any other name.
     */
    public static boolean isItemEnabled(String name) {
        if (name.startsWith(UPGRADE_PREFIX)) {
            String type = name.substring(UPGRADE_PREFIX.length());
            for (TurretBaseUpgradeType upgrade : TurretBaseUpgradeType.values()) {
                if (upgrade.getSerializedName().equals(type)) return OMTConfig.UPGRADES.isEnabled(upgrade);
            }
            for (GeneratorUpgradeType upgrade : GeneratorUpgradeType.values()) {
                if (upgrade.getSerializedName().equals(type)) return OMTConfig.UPGRADES.isEnabled(upgrade);
            }
        } else if (name.startsWith(ADDON_PREFIX)) {
            String type = name.substring(ADDON_PREFIX.length());
            for (AddonType addon : AddonType.values()) {
                if (addon.getSerializedName().equals(type)) return OMTConfig.ADDONS.isEnabled(addon);
            }
        }
        return true;
    }
}

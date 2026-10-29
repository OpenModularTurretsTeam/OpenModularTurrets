package omtteam.openmodularturrets.compat;

import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.item.ModuleToggles;

import java.util.List;

/**
 * Resource condition {@code openmodularturrets:module_enabled}: the recipe of an upgrade or addon loads only if the
 * upgrade or addon is enabled in the config ({@code upgrades.<name>}, {@code addons.<name>}). Each loader registers it
 * with its own condition API ({@code platform/<loader>}), as OpenModularLighting's {@code block_enabled}.
 */
public final class ModuleEnabledCondition {
    public static final ResourceLocation ID = OMT.id("module_enabled");
    /** JSON field with the registry paths of the upgrade and addon items. */
    public static final String ITEMS = "items";

    private ModuleEnabledCondition() {
    }

    /** Whether any of the items is enabled in the config in effect. */
    public static boolean anyEnabled(List<String> items) {
        return items.stream().anyMatch(ModuleToggles::isItemEnabled);
    }
}

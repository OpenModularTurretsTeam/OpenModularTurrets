package omtteam.openmodularturrets.config;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Validation of the ammo allow-list, the one config list with a format of its own.
 */
final class AmmoEntryValidation {
    private AmmoEntryValidation() {
    }

    /**
     * Validates ammo allow-list entries of the form {@code namespace:item[,damageBonus]} or
     * {@code #namespace:tag[,damageBonus]}.
     */
    static List<String> validate(ConfigValidator validator, String name, List<String> entries) {
        return validator.filter(name, entries, entry -> {
            String[] parts = entry.split(",", -1);
            if (parts.length > 2) return false;
            String id = parts[0].trim();
            if (id.startsWith("#")) id = id.substring(1);
            if (ResourceLocation.tryParse(id) == null) return false;
            if (parts.length == 2) {
                try {
                    Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        });
    }
}

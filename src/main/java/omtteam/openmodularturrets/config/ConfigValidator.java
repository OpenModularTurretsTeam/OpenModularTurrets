package omtteam.openmodularturrets.config;

import net.minecraft.resources.ResourceLocation;
import omtteam.openmodularturrets.OMT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Clamps config values into their allowed ranges and logs every correction.
 */
final class ConfigValidator {
    private boolean changed;

    /** Whether any value had to be corrected. */
    boolean changed() {
        return changed;
    }

    int range(String name, int value, int min, int max) {
        if (value < min || value > max) {
            int fixed = Math.max(min, Math.min(max, value));
            warn(name, value, fixed, "[" + min + ", " + max + "]");
            return fixed;
        }
        return value;
    }

    double range(String name, double value, double min, double max) {
        if (Double.isNaN(value) || value < min || value > max) {
            double fixed = Double.isNaN(value) ? min : Math.max(min, Math.min(max, value));
            warn(name, value, fixed, "[" + min + ", " + max + "]");
            return fixed;
        }
        return value;
    }

    /** Removes null entries and entries that are not valid resource locations. */
    List<String> ids(String name, List<String> ids) {
        return filter(name, ids, entry -> ResourceLocation.tryParse(entry) != null);
    }

    /**
     * Validates ammo allow-list entries of the form {@code namespace:item[,damageBonus]} or
     * {@code #namespace:tag[,damageBonus]}.
     */
    List<String> ammoEntries(String name, List<String> entries) {
        return filter(name, entries, entry -> {
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

    private List<String> filter(String name, List<String> entries, Predicate<String> valid) {
        List<String> result = new ArrayList<>();
        if (entries == null) {
            changed = true;
            return result;
        }
        for (String entry : entries) {
            if (entry != null && valid.test(entry)) {
                result.add(entry);
            } else {
                changed = true;
                OMT.LOGGER.warn("Config: removed invalid entry '{}' from {}", entry, name);
            }
        }
        return result;
    }

    private void warn(String name, Object value, Object fixed, String range) {
        changed = true;
        OMT.LOGGER.warn("Config: {} = {} is outside {}, using {}", name, value, range, fixed);
    }
}

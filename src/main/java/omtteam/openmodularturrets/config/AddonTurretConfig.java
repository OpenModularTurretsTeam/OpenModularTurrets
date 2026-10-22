package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Configuration;
import omtteam.openmodularturrets.api.TurretDefinition;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Config of the turrets other mods add through the turret API: {@code config/openmodularturrets_addon_turrets.cfg},
 * one category per turret id with the keys of a built-in turret section, filled with the definition's default stats.
 * Read once at start-up (changes need a restart, like the built-in turrets' {@code enabled}).
 */
public final class AddonTurretConfig {
    public static final String FILE_NAME = "openmodularturrets_addon_turrets.cfg";

    private static Map<String, TurretSetting> settings = Collections.emptyMap();

    private AddonTurretConfig() {
    }

    /** Reads (and completes) the file for the given turrets of other mods. */
    public static synchronized void load(File configDirectory, Collection<TurretDefinition> addonTurrets) {
        Map<String, TurretSetting> loaded = new HashMap<>();
        if (!addonTurrets.isEmpty()) {
            Configuration config = new Configuration(new File(configDirectory, FILE_NAME));
            config.load();
            for (TurretDefinition turret : addonTurrets) {
                loaded.put(turret.type().getId(), read(config, turret.type().getId(), turret.defaultStats()));
            }
            if (config.hasChanged()) {
                config.save();
            }
        }
        settings = loaded;
    }

    private static TurretSetting read(Configuration config, String category, TurretStats d) {
        return new TurretSetting(
                config.getBoolean("enabled", category, d.enabled(), "Whether the turret exists (needs a restart)"),
                config.getInt("baseRange", category, d.baseRange(), 1, 100, ""),
                config.getInt("baseFireRate", category, d.baseFireRate(), 1, 20, "Ticks between shots"),
                config.getInt("baseDamage", category, d.baseDamage(), 0, Integer.MAX_VALUE, "In half hearts"),
                config.getInt("powerUsage", category, d.powerUsage(), 0, Integer.MAX_VALUE, "RF per shot"),
                config.get(category, "baseAccuracyDeviation", d.baseAccuracyDeviation(), "In percent", 0D, 100D)
                        .getDouble(d.baseAccuracyDeviation()),
                config.getInt("maxSimultaneous", category, d.maxSimultaneous(), 1, 6, "Turrets of this kind per base"),
                number(config, "damageAmp", category, d.damageAmp()),
                number(config, "fireRateUpgrade", category, d.fireRateUpgrade()),
                config.getInt("rangeUpgrade", category, d.rangeUpgrade(), 0, 10, ""),
                number(config, "accuracyUpgrade", category, d.accuracyUpgrade()),
                number(config, "efficiencyUpgrade", category, d.efficiencyUpgrade()),
                number(config, "recyclerNegateChance", category, d.recyclerNegateChance()),
                number(config, "recyclerAddChance", category, d.recyclerAddChance()));
    }

    private static double number(Configuration config, String key, String category, double defaultValue) {
        return config.get(category, key, defaultValue, null, 0D, 1D).getDouble(defaultValue);
    }

    /** @throws IllegalArgumentException if the type is no turret of another mod */
    public static TurretSetting setting(TurretType type) {
        TurretSetting setting = settings.get(type.getId());
        if (setting == null) throw new IllegalArgumentException("No config for turret " + type);
        return setting;
    }
}

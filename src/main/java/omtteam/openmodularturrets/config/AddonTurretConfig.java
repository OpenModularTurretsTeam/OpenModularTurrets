package omtteam.openmodularturrets.config;

import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.config.Configuration;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Type;
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
    /** The type of {@link #getSettings()} for the config sync ({@code ConfigSync.registerValue}). */
    public static final Type SETTINGS_TYPE = new TypeToken<Map<String, TurretSetting>>() {
    }.getType();

    private static Map<String, TurretSetting> settings = Collections.emptyMap();

    private AddonTurretConfig() {
    }

    /**
     * Reads (and completes) the file for the given turrets of other mods.
     *
     * @param addonTurrets the turret types of other mods with the default stats of their definitions
     */
    public static synchronized void load(File configDirectory, Map<TurretType, TurretStats> addonTurrets) {
        Map<String, TurretSetting> loaded = new HashMap<>();
        if (!addonTurrets.isEmpty()) {
            // the constructor reads the file
            Configuration config = new Configuration(new File(configDirectory, FILE_NAME));
            for (Map.Entry<TurretType, TurretStats> turret : addonTurrets.entrySet()) {
                String id = turret.getKey().getId();
                loaded.put(id, read(config, id, turret.getValue()));
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
                config.getInt("baseFireRate", category, d.baseFireRate(), 1, Integer.MAX_VALUE, "Ticks between shots"),
                config.getInt("baseDamage", category, d.baseDamage(), 0, Integer.MAX_VALUE, "In half hearts"),
                config.getInt("powerUsage", category, d.powerUsage(), 0, Integer.MAX_VALUE, "RF per shot"),
                number(config, "baseAccuracyDeviation", category, d.baseAccuracyDeviation(), "In percent", 100D),
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
        return number(config, key, category, defaultValue, null, 1D);
    }

    /** A value from 0 to {@code max}; unlike {@code getInt}, Forge does not clamp doubles itself. */
    private static double number(Configuration config, String key, String category, double defaultValue,
                                 @Nullable String comment, double max) {
        double value = config.get(category, key, defaultValue, comment, 0D, max).getDouble(defaultValue);
        return Math.max(0D, Math.min(max, value));
    }

    /** The settings by turret id, as loaded (or as synced from the server). */
    public static synchronized Map<String, TurretSetting> getSettings() {
        return settings;
    }

    /** Replaces the settings (client: the server's while connected, the local ones after). */
    public static synchronized void setSettings(Map<String, TurretSetting> newSettings) {
        settings = Collections.unmodifiableMap(new HashMap<>(newSettings));
    }

    /** @throws IllegalArgumentException if the type is no turret of another mod */
    public static TurretSetting setting(TurretType type) {
        TurretSetting setting = settings.get(type.getId());
        if (setting == null) throw new IllegalArgumentException("No config for turret " + type);
        return setting;
    }
}

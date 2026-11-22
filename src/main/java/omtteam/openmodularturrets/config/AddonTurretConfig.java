package omtteam.openmodularturrets.config;

import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.config.Configuration;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Config of the turrets other mods add through the turret API: {@code config/openmodularturrets_addon_turrets.cfg},
 * one category per turret id with the keys of a built-in turret section, filled with the definition's default stats
 * (keys missing from an older file, such as the range falloff and turn rate, get the definition's defaults too: no
 * falloff and instant turning unless the turret sets them).
 * Read once at start-up (changes need a restart, like the built-in turrets' {@code enabled}).
 */
public final class AddonTurretConfig {
    public static final String FILE_NAME = "openmodularturrets_addon_turrets.cfg";
    /** The type of {@link #getSettings()} for the config sync ({@code ConfigSync.registerValue}). */
    public static final Type SETTINGS_TYPE = new TypeToken<Map<String, TurretSetting>>() {
    }.getType();

    /** Comments of the range falloff and turn rate keys, as on the built-in turrets ({@link TurretSetting}). */
    private static final String OPTIMAL_RANGE_COMMENT = "Share of the range (with range upgrades) up to which the "
            + "turret deals full damage; from there the damage falls linearly to falloffDamage at the end of the range. "
            + "1 = no falloff.";
    private static final String FALLOFF_DAMAGE_COMMENT = "Share of the damage dealt at the end of the range and beyond. "
            + "1 = no falloff.";
    private static final String TURN_RATE_COMMENT = "Degrees the turret turns per tick towards its target, on each "
            + "axis. 0 turns instantly.";
    private static final double MAX_TURN_RATE = 180D;

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
                number(config, "recyclerAddChance", category, d.recyclerAddChance()))
                .realism(
                        number(config, "optimalRange", category, d.optimalRange(), OPTIMAL_RANGE_COMMENT, 1D),
                        number(config, "falloffDamage", category, d.falloffDamage(), FALLOFF_DAMAGE_COMMENT, 1D),
                        number(config, "turnRate", category, d.turnRate(), TURN_RATE_COMMENT, MAX_TURN_RATE));
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

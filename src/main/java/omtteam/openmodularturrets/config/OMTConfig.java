package omtteam.openmodularturrets.config;

import omtteam.omlib.config.ConfigManager;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.settings.TurretStats;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The config of the mod, {@code config/openmodularturrets.json}: a facade on OMLib's {@link ConfigManager}, which
 * loads, migrates, validates and writes the file and holds the server's config on clients (the library sends it when a
 * player joins). The values are documented in the file with their description, range and default, see
 * {@link omtteam.omlib.config.ConfigWriter}.
 */
public final class OMTConfig {
    /** Default stats of the turrets added by other mods, in registration order; set by {@link #init}. */
    private static Map<TurretType, TurretStats> addonTurretDefaults = Map.of();

    /** Created after the field above: building the default config reads it. */
    private static final ConfigManager<ConfigData> MANAGER = new ConfigManager<>(OMT.MOD_ID, ConfigData.class,
            ConfigData::new, ConfigData::validate, ConfigMigrationSteps.STEPS);

    private OMTConfig() {
    }

    /**
     * The config currently in effect: the server's while connected to a remote server, otherwise the local one. Do not
     * modify the returned object.
     */
    public static ConfigData get() {
        return MANAGER.get();
    }

    /**
     * Loads the config file (writing defaults if it is missing).
     *
     * @param addonTurretDefaults the default stats of the turrets added by other mods, for their
     *                            {@code turrets.addon_turrets} entries
     */
    public static void init(Map<TurretType, TurretStats> addonTurretDefaults) {
        OMTConfig.addonTurretDefaults = Collections.unmodifiableMap(new LinkedHashMap<>(addonTurretDefaults));
        MANAGER.load();
    }

    /** The default stats of the turrets added by other mods, as passed to {@link #init}. */
    static Map<TurretType, TurretStats> addonTurretDefaults() {
        return addonTurretDefaults;
    }
}

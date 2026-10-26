package omtteam.openmodularturrets.config;

import omtteam.omlib.config.ConfigMigration;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * The config migrations of the mod, run by {@link ConfigMigration}. Add a step (and keep the older ones) when a default
 * changes; {@link #CURRENT_VERSION} follows the last step.
 */
final class ConfigMigrationSteps {
    static final List<ConfigMigration.Step> STEPS = List.of(
            new ConfigMigration.Step(2, ConfigMigrationSteps::generatorDefaults));

    static final int CURRENT_VERSION = ConfigMigration.currentVersion(STEPS);

    private ConfigMigrationSteps() {
    }

    /** Version 2: the generator's capacity and output were raised for its energy multiplier upgrades. */
    private static void generatorDefaults(JsonObject json) {
        if (json.get("miscellaneous") instanceof JsonObject misc) {
            ConfigMigration.replaceDefault(misc, "generatorCapacity", 50_000, 1_000_000);
            ConfigMigration.replaceDefault(misc, "generatorMaxOutput", 200, 65_536);
        }
    }
}

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
            new ConfigMigration.Step(2, ConfigMigrationSteps::generatorDefaults),
            new ConfigMigration.Step(3, ConfigMigrationSteps::baseChargeDefaults));

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

    /** Old defaults of {@code baseMaxCharge} of base tiers 1–5 (1.12.2's values). */
    private static final long[] OLD_BASE_CHARGE = {500, 50_000, 150_000, 500_000, 10_000_000};
    /** New defaults of {@code baseMaxCharge}: ten times the old ones, twenty times on tier 4. */
    private static final long[] NEW_BASE_CHARGE = {5_000, 500_000, 1_500_000, 10_000_000, 100_000_000};

    /** Version 3: the energy capacity of every base tier was raised (see {@link #NEW_BASE_CHARGE}). */
    private static void baseChargeDefaults(JsonObject json) {
        if (!(json.get("bases") instanceof JsonObject bases)) return;
        for (int tier = 1; tier <= OLD_BASE_CHARGE.length; tier++) {
            if (bases.get("tier_" + tier) instanceof JsonObject base) {
                ConfigMigration.replaceDefault(base, "baseMaxCharge", OLD_BASE_CHARGE[tier - 1], NEW_BASE_CHARGE[tier - 1]);
            }
        }
    }
}

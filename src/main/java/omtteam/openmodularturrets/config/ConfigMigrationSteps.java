package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Configuration;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The config migrations of the mod, run by {@link ConfigMigration} (as on 1.20.1+). Add a step (and keep the older
 * ones) when a default changes; {@link #CURRENT_VERSION} follows the last step. The base charge step has the same
 * number as on 1.20.1+, whose version 2 (the generator defaults) has no 1.12 counterpart: the 1.12 generator came with
 * its present defaults.
 */
final class ConfigMigrationSteps {
    /** 3.x section of the Relativistic Turret, now the Slowing Turret. */
    static final String OLD_SLOWING = "turrets.relativistic turret";
    static final String NEW_SLOWING = "turrets.slowing turret";

    /** Old defaults of {@code baseMaxCharge} of base tiers 1–5 (the 3.x values). */
    private static final int[] OLD_BASE_CHARGE = {500, 50_000, 150_000, 500_000, 10_000_000};
    /** New defaults of {@code baseMaxCharge}: ten times the old ones, twenty times on tier 4. */
    static final int[] NEW_BASE_CHARGE = {5_000, 500_000, 1_500_000, 10_000_000, 100_000_000};

    static final List<ConfigMigration.Step> STEPS = Collections.unmodifiableList(Arrays.asList(
            new ConfigMigration.Step(2, ConfigMigrationSteps::slowingTurretSection),
            new ConfigMigration.Step(3, ConfigMigrationSteps::baseChargeDefaults)));

    static final int CURRENT_VERSION = ConfigMigration.currentVersion(STEPS);

    private ConfigMigrationSteps() {
    }

    /** Version 2: the Relativistic Turret of 3.x is the Slowing Turret; an existing Slowing Turret section wins. */
    private static void slowingTurretSection(Configuration config) {
        boolean moves = config.hasCategory(OLD_SLOWING) && !config.hasCategory(NEW_SLOWING);
        if (ConfigMigration.moveCategory(config, OLD_SLOWING, NEW_SLOWING) && moves) {
            LogManager.getLogger(Reference.MOD_ID).info("Moved the config section '{}' to '{}'", OLD_SLOWING,
                    NEW_SLOWING);
        }
    }

    /** Version 3: the energy capacity of every base tier was raised (see {@link #NEW_BASE_CHARGE}). */
    private static void baseChargeDefaults(Configuration config) {
        for (int tier = 1; tier <= OLD_BASE_CHARGE.length; tier++) {
            ConfigMigration.replaceDefault(config, "bases.tier " + tier, "baseMaxCharge", OLD_BASE_CHARGE[tier - 1],
                    NEW_BASE_CHARGE[tier - 1]);
        }
    }
}

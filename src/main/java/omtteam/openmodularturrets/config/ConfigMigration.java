package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import omtteam.openmodularturrets.reference.Reference;
import org.apache.logging.log4j.LogManager;

import java.io.File;

/**
 * Renames config sections of 3.x in the config file before Forge reads it into {@link OMTConfig}. Must run before
 * Forge's {@code ConfigManager} loads the mod's config, i.e. in the mod's constructor.
 */
public final class ConfigMigration {
    /** 3.x section of the Relativistic Turret, now the Slowing Turret. */
    private static final String OLD_SLOWING = "turrets.relativistic turret";
    private static final String NEW_SLOWING = "turrets.slowing turret";

    private ConfigMigration() {
    }

    public static void migrate() {
        migrate(new File(Loader.instance().getConfigDir(), Reference.MOD_ID + ".cfg"));
    }

    static void migrate(File file) {
        if (!file.exists()) return;
        Configuration config = new Configuration(file);
        if (!config.hasCategory(OLD_SLOWING)) return;
        if (!config.hasCategory(NEW_SLOWING)) {
            ConfigCategory target = config.getCategory(NEW_SLOWING);
            for (Property property : config.getCategory(OLD_SLOWING).values()) {
                target.put(property.getName(), property);
            }
            LogManager.getLogger(Reference.MOD_ID).info("Moved the config section '{}' to '{}'", OLD_SLOWING,
                    NEW_SLOWING);
        }
        config.removeCategory(config.getCategory(OLD_SLOWING));
        config.save();
    }
}

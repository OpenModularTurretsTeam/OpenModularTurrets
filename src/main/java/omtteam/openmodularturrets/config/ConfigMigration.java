package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import omtteam.openmodularturrets.reference.Reference;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Updates config files written by older versions before Forge reads them into {@link OMTConfig} (1.20.1+ OMLib
 * {@code config.ConfigMigration}). Each file records its {@value #VERSION_KEY} in the {@value #VERSION_CATEGORY}
 * section; files without it (3.x and the 4.0.0 betas) are version 1. A {@link Step} only replaces values that still
 * equal the old default (see {@link #replaceDefault}), so values a server owner chose stay, and since the file is then
 * saved with the current version, it runs once per file. The steps are in {@link ConfigMigrationSteps}.
 * Must run before Forge's {@code ConfigManager} loads the mod's config, i.e. in the mod's constructor.
 */
public final class ConfigMigration {
    static final String VERSION_CATEGORY = "general";
    static final String VERSION_KEY = "configVersion";

    /** A change of the config format or defaults that files older than {@code version} get. A record in 1.20.1+. */
    static final class Step {
        private final int version;
        private final Consumer<Configuration> migrate;

        Step(int version, Consumer<Configuration> migrate) {
            this.version = version;
            this.migrate = migrate;
        }

        int version() {
            return version;
        }
    }

    private ConfigMigration() {
    }

    public static void migrate() {
        migrate(new File(Loader.instance().getConfigDir(), Reference.MOD_ID + ".cfg"));
    }

    /** Migrates the file with the mod's steps; a missing file stays missing (Forge writes it with the defaults). */
    static void migrate(File file) {
        if (!file.exists()) return;
        Configuration config = new Configuration(file);
        if (migrate(config, ConfigMigrationSteps.STEPS)) {
            config.save();
        }
    }

    /** The config version of a release with these steps: that of the last step (1 without steps). */
    static int currentVersion(List<Step> steps) {
        int current = 1;
        for (Step step : steps) {
            current = Math.max(current, step.version);
        }
        return current;
    }

    /**
     * Runs the steps newer than the file, in the order of their versions, and records the current version.
     *
     * @return whether anything changed
     */
    static boolean migrate(Configuration config, List<Step> steps) {
        int version = version(config);
        int current = currentVersion(steps);
        if (version >= current) return false;
        List<Step> sorted = new ArrayList<>(steps);
        sorted.sort(Comparator.comparingInt(Step::version));
        for (Step step : sorted) {
            if (version < step.version) step.migrate.accept(config);
        }
        config.get(VERSION_CATEGORY, VERSION_KEY, current).set(current);
        return true;
    }

    private static int version(Configuration config) {
        if (!config.hasCategory(VERSION_CATEGORY)) return 1;
        Property version = config.getCategory(VERSION_CATEGORY).get(VERSION_KEY);
        return version != null && version.isIntValue() ? version.getInt() : 1;
    }

    /** Sets {@code key} of {@code category} to {@code newDefault} if it is a number that still equals {@code oldDefault}. */
    static void replaceDefault(Configuration config, String category, String key, int oldDefault, int newDefault) {
        if (!config.hasCategory(category)) return;
        Property property = config.getCategory(category).get(key);
        if (property != null && property.isIntValue() && property.getInt() == oldDefault) {
            property.set(newDefault);
        }
    }

    /**
     * Replaces the entry {@code oldEntry} of the list {@code key} of {@code category} with {@code newEntry}, if the list
     * still contains it; the other entries and their order stay.
     */
    static void replaceListEntry(Configuration config, String category, String key, String oldEntry, String newEntry) {
        if (!config.hasCategory(category)) return;
        Property property = config.getCategory(category).get(key);
        if (property == null || !property.isList()) return;
        String[] entries = property.getStringList();
        boolean found = false;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].trim().equals(oldEntry)) {
                entries[i] = newEntry;
                found = true;
            }
        }
        if (found) property.set(entries);
    }

    /** Moves the properties of section {@code from} to {@code to} (unless {@code to} exists already) and removes it. */
    static boolean moveCategory(Configuration config, String from, String to) {
        if (!config.hasCategory(from)) return false;
        if (!config.hasCategory(to)) {
            ConfigCategory target = config.getCategory(to);
            for (Property property : config.getCategory(from).values()) {
                target.put(property.getName(), property);
            }
        }
        config.removeCategory(config.getCategory(from));
        return true;
    }
}

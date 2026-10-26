package omtteam.openmodularturrets.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Updates config files written by older versions. Each file records its {@code configVersion}; files without it are
 * version 1. A {@link Step} only replaces values that still equal the old default (see {@link #replaceDefault}), so
 * values a server owner chose stay, and since the file is then saved with the current version, it runs once per file.
 * The steps of the mod are in {@code ConfigMigrationSteps}.
 */
final class ConfigMigration {
    static final String VERSION_KEY = "configVersion";

    /** A change of the config format or defaults that files older than {@code version} get. */
    record Step(int version, Consumer<JsonObject> migrate) {
    }

    private ConfigMigration() {
    }

    /** The config version of a release with these steps: that of the last step (1 without steps). */
    static int currentVersion(List<Step> steps) {
        return steps.stream().mapToInt(Step::version).max().orElse(1);
    }

    /**
     * Migrates the JSON of a config file in place: runs the steps newer than the file, in the order of their versions,
     * and records the current version.
     *
     * @return whether anything changed
     */
    static boolean migrate(JsonObject json, List<Step> steps) {
        int version = version(json);
        int current = currentVersion(steps);
        if (version >= current) return false;
        steps.stream().sorted(Comparator.comparingInt(Step::version)).forEach(step -> {
            if (version < step.version()) step.migrate().accept(json);
        });
        json.addProperty(VERSION_KEY, current);
        return true;
    }

    private static int version(JsonObject json) {
        JsonElement version = json.get(VERSION_KEY);
        return version instanceof JsonPrimitive primitive && primitive.isNumber() ? primitive.getAsInt() : 1;
    }

    /** Sets {@code key} to {@code newDefault} if it is a number that still equals {@code oldDefault}. */
    static void replaceDefault(JsonObject section, String key, long oldDefault, long newDefault) {
        JsonElement value = section.get(key);
        if (value instanceof JsonPrimitive primitive && primitive.isNumber() && primitive.getAsLong() == oldDefault) {
            section.addProperty(key, newDefault);
        }
    }

    /** Like the {@code long} variant, for decimal values. */
    static void replaceDefault(JsonObject section, String key, double oldDefault, double newDefault) {
        JsonElement value = section.get(key);
        if (value instanceof JsonPrimitive primitive && primitive.isNumber() && primitive.getAsDouble() == oldDefault) {
            section.addProperty(key, newDefault);
        }
    }
}

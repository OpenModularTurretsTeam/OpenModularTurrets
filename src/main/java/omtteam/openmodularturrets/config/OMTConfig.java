package omtteam.openmodularturrets.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.platform.Platform;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Loads, validates and saves {@code config/openmodularturrets.json}, and holds the server's config on clients
 * ({@code network.ConfigSync} sends it).
 * <p>
 * Missing keys are filled in with defaults, unknown keys are dropped and out-of-range values are clamped; the file
 * is rewritten whenever that happens. The file is JSON with {@code //} comments (read leniently): every key is
 * documented with its description, range and default, see {@link ConfigWriter}. The file is also rewritten when it
 * differs from what the mod would write, which refreshes the comments after an update; comments added by users are
 * not kept.
 */
public final class OMTConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final String FILE_NAME = OMT.MOD_ID + ".json";

    /** Default stats of the turrets added by other mods, in registration order; set by {@link #init}. */
    private static Map<TurretType, TurretStats> addonTurretDefaults = Map.of();
    /** Config loaded from this instance's own file. */
    private static ConfigData local = new ConfigData();
    /** Config in effect: the server's while connected to a remote server, otherwise {@link #local}. */
    private static volatile ConfigData active = local;

    private OMTConfig() {
    }

    /**
     * The config currently in effect. Do not modify the returned object.
     */
    public static ConfigData get() {
        return active;
    }

    /**
     * Loads the config file (writing defaults if it is missing).
     *
     * @param addonTurretDefaults the default stats of the turrets added by other mods, for their
     *                            {@code turrets.addon_turrets} entries
     */
    public static void init(Map<TurretType, TurretStats> addonTurretDefaults) {
        OMTConfig.addonTurretDefaults = Collections.unmodifiableMap(new LinkedHashMap<>(addonTurretDefaults));
        load();
    }

    /** The default stats of the turrets added by other mods, as passed to {@link #init}. */
    static Map<TurretType, TurretStats> addonTurretDefaults() {
        return addonTurretDefaults;
    }

    /**
     * The config loaded from this instance's own file as JSON, which the server sends to joining clients
     * ({@code network.ConfigSync}).
     */
    public static String localJson() {
        return GSON.toJson(local);
    }

    public static void load() {
        Path path = Platform.loaderInfo().configDir().resolve(FILE_NAME);
        ConfigData data;
        boolean changed;
        if (Files.exists(path)) {
            JsonElement json;
            try (Reader reader = Files.newBufferedReader(path)) {
                json = JsonParser.parseReader(reader);
            } catch (Exception e) {
                OMT.LOGGER.error("Could not read {}, using defaults (the file is left untouched)", path, e);
                local = active = validatedDefaults();
                return;
            }
            ParseResult result = parse(json);
            data = result.data();
            changed = result.changed() || !commented(data).equals(readText(path));
        } else {
            data = validatedDefaults();
            changed = true;
        }
        if (changed) {
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, commented(data));
            } catch (IOException e) {
                OMT.LOGGER.error("Could not write {}", path, e);
            }
        }
        local = active = data;
    }

    /** The defaults with their lists parsed (ammo allow list, entity lists), as after loading a file. */
    private static ConfigData validatedDefaults() {
        ConfigData data = new ConfigData();
        data.validate(new ConfigValidator());
        return data;
    }

    /** The file contents for {@code data}: JSON with the comments from {@link Comment} and {@link Range}. */
    private static String commented(ConfigData data) {
        return ConfigWriter.write(data, new ConfigData());
    }

    private static String readText(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Client side: use the config received from the server.
     */
    public static void applyServerConfig(String json) {
        try {
            active = parse(JsonParser.parseString(json)).data();
        } catch (Exception e) {
            OMT.LOGGER.error("Received an invalid config from the server, keeping the local one", e);
        }
    }

    /**
     * Client side: go back to the local config, e.g. after disconnecting.
     */
    public static void useLocalConfig() {
        active = local;
    }

    private record ParseResult(ConfigData data, boolean changed) {
    }

    private static ParseResult parse(JsonElement json) {
        JsonObject defaults = GSON.toJsonTree(new ConfigData()).getAsJsonObject();
        boolean[] changed = {false};
        // before merging: a file without a version gets the current one from the defaults
        if (json.isJsonObject()) changed[0] = ConfigMigration.migrate(json.getAsJsonObject(), ConfigMigrationSteps.STEPS);
        JsonElement merged = json.isJsonObject() ? merge(defaults, json.getAsJsonObject(), "", changed) : defaults;
        if (!json.isJsonObject()) {
            OMT.LOGGER.warn("Config: root is not a JSON object, using defaults");
            changed[0] = true;
        }
        ConfigData data = GSON.fromJson(merged, ConfigData.class);
        ConfigValidator validator = new ConfigValidator();
        data.validate(validator);
        return new ParseResult(data, changed[0] || validator.changed());
    }

    /**
     * Overlays {@code user} onto {@code defaults}: keeps user values whose JSON type matches the default,
     * falls back to the default otherwise and drops unknown keys.
     */
    private static JsonObject merge(JsonObject defaults, JsonObject user, String path, boolean[] changed) {
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
            String key = entry.getKey();
            JsonElement def = entry.getValue();
            JsonElement value = user.get(key);
            if (value == null) {
                changed[0] = true;
                result.add(key, def);
            } else if (def.isJsonObject() && value.isJsonObject()) {
                result.add(key, merge(def.getAsJsonObject(), value.getAsJsonObject(), path + key + ".", changed));
            } else if (sameType(def, value)) {
                result.add(key, value);
            } else {
                OMT.LOGGER.warn("Config: {}{} has the wrong type, using the default {}", path, key, def);
                changed[0] = true;
                result.add(key, def);
            }
        }
        for (String key : user.keySet()) {
            if (!defaults.has(key)) {
                OMT.LOGGER.warn("Config: removed unknown key {}{}", path, key);
                changed[0] = true;
            }
        }
        return result;
    }

    private static boolean sameType(JsonElement a, JsonElement b) {
        if (a.isJsonArray()) return b.isJsonArray();
        if (a.isJsonPrimitive() && b.isJsonPrimitive()) {
            JsonPrimitive pa = a.getAsJsonPrimitive();
            JsonPrimitive pb = b.getAsJsonPrimitive();
            return pa.isBoolean() == pb.isBoolean() && pa.isNumber() == pb.isNumber() && pa.isString() == pb.isString();
        }
        return false;
    }
}

package omtteam.openmodularturrets.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigrationTest {
    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    @Test
    void oldGeneratorDefaultsAreRaised() {
        JsonObject config = json("{\"miscellaneous\": {\"generatorCapacity\": 50000, \"generatorMaxOutput\": 200}}");
        assertTrue(ConfigMigration.migrate(config));
        JsonObject misc = config.getAsJsonObject("miscellaneous");
        assertEquals(1_000_000, misc.get("generatorCapacity").getAsInt());
        assertEquals(65_536, misc.get("generatorMaxOutput").getAsInt());
        assertEquals(ConfigMigration.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }

    @Test
    void chosenValuesStay() {
        JsonObject config = json("{\"miscellaneous\": {\"generatorCapacity\": 80000, \"generatorMaxOutput\": 500}}");
        ConfigMigration.migrate(config);
        JsonObject misc = config.getAsJsonObject("miscellaneous");
        assertEquals(80_000, misc.get("generatorCapacity").getAsInt());
        assertEquals(500, misc.get("generatorMaxOutput").getAsInt());
    }

    @Test
    void runsOncePerFile() {
        JsonObject config = json("{\"configVersion\": 2, \"miscellaneous\": {\"generatorCapacity\": 50000}}");
        assertFalse(ConfigMigration.migrate(config), "an owner may set the old value again");
        assertEquals(50_000, config.getAsJsonObject("miscellaneous").get("generatorCapacity").getAsInt());
    }

    @Test
    void filesWithoutTheSectionOnlyGetTheVersion() {
        JsonObject config = json("{\"configVersion\": \"x\"}");
        assertTrue(ConfigMigration.migrate(config), "an invalid version counts as version 1");
        assertEquals(ConfigMigration.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }
}

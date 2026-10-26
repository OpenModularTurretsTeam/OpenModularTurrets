package omtteam.openmodularturrets.config;

import omtteam.omlib.config.ConfigMigration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigrationStepsTest {
    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    private static boolean migrate(JsonObject config) {
        return ConfigMigration.migrate(config, ConfigMigrationSteps.STEPS);
    }

    @Test
    void oldGeneratorDefaultsAreRaised() {
        JsonObject config = json("{\"miscellaneous\": {\"generatorCapacity\": 50000, \"generatorMaxOutput\": 200}}");
        assertTrue(migrate(config));
        JsonObject misc = config.getAsJsonObject("miscellaneous");
        assertEquals(1_000_000, misc.get("generatorCapacity").getAsInt());
        assertEquals(65_536, misc.get("generatorMaxOutput").getAsInt());
        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }

    @Test
    void chosenValuesStay() {
        JsonObject config = json("{\"miscellaneous\": {\"generatorCapacity\": 80000, \"generatorMaxOutput\": 500}}");
        migrate(config);
        JsonObject misc = config.getAsJsonObject("miscellaneous");
        assertEquals(80_000, misc.get("generatorCapacity").getAsInt());
        assertEquals(500, misc.get("generatorMaxOutput").getAsInt());
    }

    @Test
    void runsOncePerFile() {
        JsonObject config = json("{\"configVersion\": 2, \"miscellaneous\": {\"generatorCapacity\": 50000}}");
        assertFalse(migrate(config), "an owner may set the old value again");
        assertEquals(50_000, config.getAsJsonObject("miscellaneous").get("generatorCapacity").getAsInt());
    }

    @Test
    void filesWithoutTheSectionOnlyGetTheVersion() {
        JsonObject config = json("{\"configVersion\": \"x\"}");
        assertTrue(migrate(config), "an invalid version counts as version 1");
        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }
}

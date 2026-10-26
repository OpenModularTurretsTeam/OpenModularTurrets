package omtteam.openmodularturrets.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigMigrationTest {
    private static JsonObject json(String text) {
        return JsonParser.parseString(text).getAsJsonObject();
    }

    private static ConfigMigration.Step marker(List<Integer> ran, int version) {
        return new ConfigMigration.Step(version, config -> ran.add(version));
    }

    @Test
    void currentVersionIsTheLastStep() {
        assertEquals(1, ConfigMigration.currentVersion(List.of()));
        assertEquals(4, ConfigMigration.currentVersion(List.of(new ConfigMigration.Step(4, c -> { }),
                new ConfigMigration.Step(2, c -> { }))));
    }

    @Test
    void onlyStepsNewerThanTheFileRunInOrder() {
        List<Integer> ran = new ArrayList<>();
        JsonObject config = json("{\"configVersion\": 2}");
        assertTrue(ConfigMigration.migrate(config, List.of(marker(ran, 4), marker(ran, 2), marker(ran, 3))));
        assertEquals(List.of(3, 4), ran);
        assertEquals(4, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }

    @Test
    void filesWithoutAVersionAreVersionOne() {
        List<Integer> ran = new ArrayList<>();
        assertTrue(ConfigMigration.migrate(json("{}"), List.of(marker(ran, 2), marker(ran, 3))));
        assertEquals(List.of(2, 3), ran);
    }

    @Test
    void invalidVersionsCountAsVersionOne() {
        JsonObject config = json("{\"configVersion\": \"x\"}");
        assertTrue(ConfigMigration.migrate(config, List.of(marker(new ArrayList<>(), 2))));
        assertEquals(2, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }

    @Test
    void currentFilesStayUntouched() {
        List<Integer> ran = new ArrayList<>();
        JsonObject config = json("{\"configVersion\": 3}");
        assertFalse(ConfigMigration.migrate(config, List.of(marker(ran, 2), marker(ran, 3))));
        assertTrue(ran.isEmpty());
        assertEquals(json("{\"configVersion\": 3}"), config);
    }

    @Test
    void replaceDefaultOnlyChangesOldDefaults() {
        JsonObject section = json("{\"old\": 50, \"chosen\": 70, \"text\": \"50\", \"decimal\": 0.05, \"other\": 0.2}");
        ConfigMigration.replaceDefault(section, "old", 50, 100);
        ConfigMigration.replaceDefault(section, "chosen", 50, 100);
        ConfigMigration.replaceDefault(section, "text", 50, 100);
        ConfigMigration.replaceDefault(section, "missing", 50, 100);
        ConfigMigration.replaceDefault(section, "decimal", 0.05, 0.1);
        ConfigMigration.replaceDefault(section, "other", 0.05, 0.1);
        assertEquals(100, section.get("old").getAsInt());
        assertEquals("100", section.get("old").getAsString(), "an integer stays an integer");
        assertEquals(70, section.get("chosen").getAsInt());
        assertEquals("50", section.get("text").getAsString());
        assertFalse(section.has("missing"));
        assertEquals(0.1, section.get("decimal").getAsDouble());
        assertEquals(0.2, section.get("other").getAsDouble());
    }
}

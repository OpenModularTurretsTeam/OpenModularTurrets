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
        JsonObject config = json("{\"configVersion\": 3, \"miscellaneous\": {\"generatorCapacity\": 50000},"
                + " \"bases\": {\"tier_1\": {\"baseMaxCharge\": 500}}}");
        assertFalse(migrate(config), "an owner may set the old value again");
        assertEquals(50_000, config.getAsJsonObject("miscellaneous").get("generatorCapacity").getAsInt());
        assertEquals(500, baseCharge(config, 1));
    }

    @Test
    void oldBaseChargeDefaultsAreRaised() {
        JsonObject config = json("{\"configVersion\": 2, \"bases\": {\"tier_1\": {\"baseMaxCharge\": 500},"
                + " \"tier_2\": {\"baseMaxCharge\": 50000}, \"tier_3\": {\"baseMaxCharge\": 150000},"
                + " \"tier_4\": {\"baseMaxCharge\": 500000}, \"tier_5\": {\"baseMaxCharge\": 10000000}}}");
        assertTrue(migrate(config));
        long[] expected = {5_000, 500_000, 1_500_000, 10_000_000, 100_000_000};
        for (int tier = 1; tier <= 5; tier++) {
            assertEquals(expected[tier - 1], baseCharge(config, tier), "tier " + tier);
        }
        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }

    @Test
    void chosenBaseChargesStayAndVersionTwoKeepsItsGenerator() {
        JsonObject config = json("{\"configVersion\": 2, \"miscellaneous\": {\"generatorCapacity\": 50000},"
                + " \"bases\": {\"tier_1\": {\"baseMaxCharge\": 800}, \"tier_2\": {\"baseMaxCharge\": 500}}}");
        assertTrue(migrate(config));
        assertEquals(800, baseCharge(config, 1));
        assertEquals(500, baseCharge(config, 2), "tier 1's old default is no default of tier 2");
        assertEquals(50_000, config.getAsJsonObject("miscellaneous").get("generatorCapacity").getAsInt(),
                "the version 2 step ran again");
    }

    private static long baseCharge(JsonObject config, int tier) {
        return config.getAsJsonObject("bases").getAsJsonObject("tier_" + tier).get("baseMaxCharge").getAsLong();
    }

    @Test
    void filesWithoutTheSectionOnlyGetTheVersion() {
        JsonObject config = json("{\"configVersion\": \"x\"}");
        assertTrue(migrate(config), "an invalid version counts as version 1");
        assertEquals(ConfigMigrationSteps.CURRENT_VERSION, config.get(ConfigMigration.VERSION_KEY).getAsInt());
    }
}

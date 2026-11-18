package omtteam.openmodularturrets.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.common.crafting.JsonContext;
import omtteam.openmodularturrets.core.item.AddonType;
import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;
import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleEnabledConditionFactoryTest {
    private static final Path RECIPES = Paths.get("src/main/resources/assets/openmodularturrets/recipes");

    @AfterEach
    void restoreSwitches() {
        OMTConfig.UPGRADES = new OMTConfig.ConfigUpgrades();
        OMTConfig.ADDONS = new OMTConfig.ConfigAddons();
    }

    private static boolean parse(String json) {
        JsonObject object = new JsonParser().parse(json).getAsJsonObject();
        return new ModuleEnabledConditionFactory().parse(new JsonContext("openmodularturrets"), object).getAsBoolean();
    }

    @Test
    void everythingIsEnabledByDefault() {
        for (TurretBaseUpgradeType type : TurretBaseUpgradeType.values()) {
            assertTrue(OMTConfig.UPGRADES.isEnabled(type), type.name());
        }
        assertTrue(OMTConfig.UPGRADES.isEnabled(GeneratorUpgradeType.ENERGY_MULTIPLIER));
        for (AddonType type : AddonType.values()) {
            assertTrue(OMTConfig.ADDONS.isEnabled(type), type.name());
        }
    }

    @Test
    void conditionFollowsTheSwitches() {
        String range = "{\"type\": \"openmodularturrets:module_enabled\", \"items\": [\"upgrade_range\"]}";
        assertTrue(parse(range));
        OMTConfig.UPGRADES.range = false;
        assertFalse(parse(range));
        assertTrue(parse("{\"items\": [\"upgrade_range\", \"upgrade_accuracy\"]}"), "one enabled item is enough");
        OMTConfig.UPGRADES.energyMultiplier = false;
        OMTConfig.ADDONS.serialPort = false;
        assertFalse(ModuleEnabledConditionFactory.isItemEnabled("upgrade_energy_multiplier"));
        assertFalse(ModuleEnabledConditionFactory.isItemEnabled("addon_serial_port"));
        assertTrue(ModuleEnabledConditionFactory.isItemEnabled("addon_damage_amp"), "the others stay enabled");
        assertTrue(ModuleEnabledConditionFactory.isItemEnabled("addon_potentia"), "the potentia addon has no switch");
        assertTrue(ModuleEnabledConditionFactory.isItemEnabled("upgrade_unknown"), "unknown names are enabled");
        assertTrue(ModuleEnabledConditionFactory.isItemEnabled("turret_base"), "other items are enabled");
    }

    /** Every upgrade and addon recipe names its item in the condition: with every switch off none of them loads. */
    @Test
    void everyModuleRecipeHasTheCondition() throws IOException {
        for (Field field : OMTConfig.ConfigUpgrades.class.getFields()) {
            if (field.getType() == boolean.class) setFalse(field, OMTConfig.UPGRADES);
        }
        for (Field field : OMTConfig.ConfigAddons.class.getFields()) {
            if (field.getType() == boolean.class) setFalse(field, OMTConfig.ADDONS);
        }
        List<String> checked = new ArrayList<>();
        try (DirectoryStream<Path> files = Files.newDirectoryStream(RECIPES, "{upgrade,addon}_*.json")) {
            for (Path file : files) {
                JsonObject recipe;
                try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    recipe = new JsonParser().parse(reader).getAsJsonObject();
                }
                boolean loads = true;
                for (JsonElement condition : recipe.getAsJsonArray("conditions")) {
                    JsonObject object = condition.getAsJsonObject();
                    if (object.get("type").getAsString().equals("openmodularturrets:module_enabled")) {
                        loads &= parse(object.toString());
                    }
                }
                assertFalse(loads, file.getFileName() + " still loads with every switch off");
                checked.add(file.getFileName().toString());
            }
        }
        Collections.sort(checked);
        // 5 upgrades and 6 addons in three recipe sets, the serial port (computer) and the energy multiplier
        assertEquals(35, checked.size(), Arrays.toString(checked.toArray()));
    }

    private static void setFalse(Field field, Object section) {
        try {
            field.setBoolean(section, false);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }
}

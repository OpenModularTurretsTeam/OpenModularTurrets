package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Config;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigLogicSettingsTest {
    private String[] blocked;
    private String[] hostile;
    private String[] neutral;

    @BeforeEach
    void saveLists() {
        blocked = OMTConfig.GENERAL.stringMobBlackList;
        hostile = OMTConfig.GENERAL.stringMobForceList;
        neutral = OMTConfig.GENERAL.stringNeutralForceList;
    }

    @AfterEach
    void restoreLists() {
        OMTConfig.GENERAL.stringMobBlackList = blocked;
        OMTConfig.GENERAL.stringMobForceList = hostile;
        OMTConfig.GENERAL.stringNeutralForceList = neutral;
    }

    @Test
    void everyBuiltInTurretHasItsOwnConfigSection() {
        Map<TurretSetting, TurretType> sections = new IdentityHashMap<>();
        for (TurretType type : TurretType.values()) {
            if (!type.isBuiltIn()) continue;
            TurretType other = sections.put(ConfigLogicSettings.setting(type), type);
            assertNull(other, type + " shares its section with " + other);
        }
        assertEquals(14, sections.size());
    }

    @Test
    void theDefaultStatsAreTheConfigDefaults() {
        assertEquals(ConfigLogicSettings.toStats(new OMTConfig.ConfigTurrets().laser_turret),
                ConfigLogicSettings.defaultStats(TurretType.LASER_TURRET));
        assertThrows(IllegalArgumentException.class,
                () -> ConfigLogicSettings.defaultStats(TurretType.builder("othermod", "x_turret").build()));
    }

    @Test
    void baseTiersOneToFive() {
        assertSame(OMTConfig.BASES.baseTierOne, ConfigLogicSettings.baseSetting(1));
        assertSame(OMTConfig.BASES.baseTierFive, ConfigLogicSettings.baseSetting(5));
        assertEquals(OMTConfig.BASES.baseTierThree.baseMaxCharge,
                ConfigLogicSettings.INSTANCE.baseTier(3).maxCharge());
        assertThrows(IllegalArgumentException.class, () -> ConfigLogicSettings.baseSetting(0));
        assertThrows(IllegalArgumentException.class, () -> ConfigLogicSettings.baseSetting(6));
    }

    @Test
    void entityListsDefaultToTheVanillaNamespace() {
        OMTConfig.GENERAL.stringMobBlackList = new String[]{" zombie ", "othermod:robot"};
        assertEquals(new HashSet<>(Arrays.asList("minecraft:zombie", "othermod:robot")),
                ConfigLogicSettings.INSTANCE.blockedEntities());
    }

    @Test
    void entityListsFollowConfigChanges() {
        ConfigLogicSettings settings = ConfigLogicSettings.INSTANCE;
        // Forge replaces the arrays when the config changes; each list is read on its own
        for (String id : new String[]{"minecraft:creeper", "minecraft:spider"}) {
            OMTConfig.GENERAL.stringMobBlackList = new String[]{id};
            assertEquals(Collections.singleton(id), settings.blockedEntities(), "blocked");
        }
        for (String id : new String[]{"minecraft:creeper", "minecraft:spider"}) {
            OMTConfig.GENERAL.stringMobForceList = new String[]{id};
            assertEquals(Collections.singleton(id), settings.forcedHostileEntities(), "hostile");
        }
        for (String id : new String[]{"minecraft:creeper", "minecraft:spider"}) {
            OMTConfig.GENERAL.stringNeutralForceList = new String[]{id};
            assertEquals(Collections.singleton(id), settings.forcedNeutralEntities(), "neutral");
        }
    }

    @Test
    void everyDefaultIsInsideItsConfigRange() throws IllegalAccessException {
        List<String> outside = new ArrayList<>();
        for (Object section : new Object[]{new OMTConfig.ConfigTurrets(), new OMTConfig.ConfigBases(),
                new OMTConfig.ConfigGeneral(), new OMTConfig.ConfigMisc()}) {
            collectOutside(section, section.getClass().getSimpleName(), outside);
        }
        assertTrue(outside.isEmpty(), "outside their range: " + outside);
    }

    private static void collectOutside(Object section, String path, List<String> outside)
            throws IllegalAccessException {
        for (Field field : section.getClass().getFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            String name = path + "." + field.getName();
            Object value = field.get(section);
            Config.RangeInt ints = field.getAnnotation(Config.RangeInt.class);
            Config.RangeDouble doubles = field.getAnnotation(Config.RangeDouble.class);
            if (ints != null && ((Integer) value < ints.min() || (Integer) value > ints.max())
                    || doubles != null && ((Double) value < doubles.min() || (Double) value > doubles.max())) {
                outside.add(name + " = " + value);
            } else if (value instanceof TurretSetting || value instanceof BaseSetting) {
                collectOutside(value, name, outside);
            }
        }
    }
}

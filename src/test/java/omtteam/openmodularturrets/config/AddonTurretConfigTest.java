package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Configuration;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddonTurretConfigTest {
    private static final TurretType TYPE = TurretType.builder("othermod", "config_turret").build();
    private static final TurretStats DEFAULTS = TurretStats.builder(12, 60, 4, 300).accuracyDeviation(25)
            .maxSimultaneous(2).damageAmp(0.3D).falloff(0.8D, 0.5D).turnRate(30).build();

    @TempDir
    Path directory;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        ForgeConfigFiles.setMinecraftHome(directory.toFile());
    }

    @AfterEach
    void tearDown() {
        AddonTurretConfig.load(directory.toFile(), Collections.emptyMap());
    }

    private File file() {
        return new File(directory.toFile(), AddonTurretConfig.FILE_NAME);
    }

    private TurretSetting load() {
        AddonTurretConfig.load(directory.toFile(),
                Collections.singletonMap(TYPE, DEFAULTS));
        return AddonTurretConfig.setting(TYPE);
    }

    @Test
    void writesTheDefaultStatsOfANewTurret() {
        TurretSetting setting = load();

        assertTrue(file().exists());
        assertEquals(DEFAULTS, ConfigLogicSettings.toStats(setting), "a slow fire rate like the railgun's is kept");
        Configuration written = new Configuration(file());
        assertEquals(300, written.getCategory(TYPE.getId()).get("powerUsage").getInt());
    }

    @Test
    void readsChangedValues() {
        load();
        Configuration config = new Configuration(file());
        config.getCategory(TYPE.getId()).get("baseRange").set(40);
        config.getCategory(TYPE.getId()).get("damageAmp").set(0.5D);
        config.save();

        TurretSetting setting = load();

        assertEquals(40, setting.baseRange);
        assertEquals(0.5D, setting.damageAmp);
    }

    @Test
    void valuesOutsideTheirRangeAreClamped() {
        load();
        Configuration config = new Configuration(file());
        config.getCategory(TYPE.getId()).get("baseRange").set(500);
        config.getCategory(TYPE.getId()).get("maxSimultaneous").set(0);
        config.getCategory(TYPE.getId()).get("damageAmp").set(5D);
        config.getCategory(TYPE.getId()).get("recyclerAddChance").set(-1D);
        config.getCategory(TYPE.getId()).get("baseAccuracyDeviation").set(250D);
        config.save();

        TurretSetting setting = load();

        assertEquals(100, setting.baseRange);
        assertEquals(1, setting.maxSimultaneous);
        assertEquals(1D, setting.damageAmp);
        assertEquals(0D, setting.recyclerAddChance);
        assertEquals(100D, setting.baseAccuracyDeviation);
    }

    /** The range falloff and turn rate keys (issue 539): written with the definition's values, read and clamped. */
    @Test
    void theRangeFalloffAndTurnRateKeysAreWrittenReadAndClamped() {
        load();
        Configuration written = new Configuration(file());
        assertEquals(0.8D, written.getCategory(TYPE.getId()).get("optimalRange").getDouble());
        assertEquals(0.5D, written.getCategory(TYPE.getId()).get("falloffDamage").getDouble());
        assertEquals(30D, written.getCategory(TYPE.getId()).get("turnRate").getDouble());

        written.getCategory(TYPE.getId()).get("optimalRange").set(0.25D);
        written.getCategory(TYPE.getId()).get("falloffDamage").set(-1D);
        written.getCategory(TYPE.getId()).get("turnRate").set(500D);
        written.save();
        TurretSetting setting = load();

        assertEquals(0.25D, setting.optimalRange);
        assertEquals(0D, setting.falloffDamage);
        assertEquals(180D, setting.turnRate);

        written = new Configuration(file());
        written.getCategory(TYPE.getId()).get("optimalRange").set(2D);
        written.save();
        assertEquals(1D, load().optimalRange);
    }

    /** A file from before the range falloff gets the new keys with the definition's values. */
    @Test
    void anOlderFileGetsTheNewKeysWithTheDefinitionsValues() {
        load();
        Configuration config = new Configuration(file());
        config.getCategory(TYPE.getId()).remove("optimalRange");
        config.getCategory(TYPE.getId()).remove("falloffDamage");
        config.getCategory(TYPE.getId()).remove("turnRate");
        config.save();

        TurretSetting setting = load();

        assertEquals(DEFAULTS, ConfigLogicSettings.toStats(setting));
        assertTrue(new Configuration(file()).getCategory(TYPE.getId()).containsKey("turnRate"));
    }

    @Test
    void noAddonTurretsWriteNoFile() {
        AddonTurretConfig.load(directory.toFile(), Collections.emptyMap());
        assertFalse(file().exists());
    }

    @Test
    void unknownTurretsHaveNoSetting() {
        load();
        assertThrows(IllegalArgumentException.class,
                () -> AddonTurretConfig.setting(TurretType.builder("othermod", "unknown_turret").build()));
    }
}

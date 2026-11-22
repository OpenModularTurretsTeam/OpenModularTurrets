package omtteam.openmodularturrets.config;

import com.google.gson.Gson;
import net.minecraftforge.common.config.Config;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** The defaults of the range falloff and the turn rate (issue 539), and their way from the config to the stats. */
class TurretConfigDefaultsTest {
    private static final double DELTA = 1.0E-9;

    /** optimalRange, falloffDamage and turnRate of a built-in turret. */
    private static final class Realism {
        final double optimalRange;
        final double falloffDamage;
        final double turnRate;

        Realism(double optimalRange, double falloffDamage, double turnRate) {
            this.optimalRange = optimalRange;
            this.falloffDamage = falloffDamage;
            this.turnRate = turnRate;
        }
    }

    /**
     * The defaults of every built-in turret, the same as on 1.20.1, 1.21.1 and 1.21.11. The arc, slowing, teleporter
     * and melee turrets turn instantly, because their models do not turn.
     */
    private static final Map<TurretType, Realism> DEFAULTS = defaults();

    private static Map<TurretType, Realism> defaults() {
        Map<TurretType, Realism> map = new LinkedHashMap<>();
        map.put(TurretType.DISPOSABLE_TURRET, new Realism(0.5, 0.5, 10));
        map.put(TurretType.POTATO_CANNON_TURRET, new Realism(0.5, 0.5, 10));
        map.put(TurretType.CROSSBOW_TURRET, new Realism(0.7, 0.6, 12));
        map.put(TurretType.MACHINE_GUN_TURRET, new Realism(0.6, 0.5, 20));
        map.put(TurretType.INCENDIARY_TURRET, new Realism(0.6, 0.4, 15));
        map.put(TurretType.GRENADE_TURRET, new Realism(1, 1, 8));
        map.put(TurretType.ROCKET_TURRET, new Realism(1, 1, 8));
        map.put(TurretType.LASER_TURRET, new Realism(0.5, 0.5, 15));
        map.put(TurretType.ARC_TURRET, new Realism(0.5, 0.3, 0));
        map.put(TurretType.PLASMA_TURRET, new Realism(0.7, 0.5, 6));
        map.put(TurretType.RAILGUN_TURRET, new Realism(1, 1, 3));
        map.put(TurretType.SLOWING_TURRET, new Realism(1, 1, 0));
        map.put(TurretType.TELEPORTER_TURRET, new Realism(1, 1, 0));
        map.put(TurretType.MELEE_TURRET, new Realism(1, 1, 0));
        return map;
    }

    @Test
    void everyBuiltInTurretHasItsRealismDefaults() {
        int checked = 0;
        for (TurretType type : TurretType.values()) {
            if (!type.isBuiltIn()) continue;
            Realism expected = DEFAULTS.get(type);
            assertNotNull(expected, "No expected defaults for " + type);
            TurretStats stats = ConfigLogicSettings.defaultStats(type);
            assertEquals(expected.optimalRange, stats.optimalRange(), DELTA, "optimalRange of " + type);
            assertEquals(expected.falloffDamage, stats.falloffDamage(), DELTA, "falloffDamage of " + type);
            assertEquals(expected.turnRate, stats.turnRate(), DELTA, "turnRate of " + type);
            checked++;
        }
        assertEquals(DEFAULTS.size(), checked);
    }

    /** All 17 stats reach the core: an un-updated {@code toStats} would drop the last three silently. */
    @Test
    void toStatsKeepsEveryStat() {
        TurretSetting setting = new TurretSetting(false, 21, 17, 9, 1234, 3.5D, 2, 0.07D, 0.15D, 3, 0.25D, 0.09D, 0.12D,
                0.06D).realism(0.4D, 0.3D, 7.5D);
        assertEquals(new TurretStats(false, 21, 17, 9, 1234, 3.5D, 2, 0.07D, 0.15D, 3, 0.25D, 0.09D, 0.12D, 0.06D, 0.4D,
                0.3D, 7.5D), ConfigLogicSettings.toStats(setting));
    }

    /** A setting from the 14-value constructor has no falloff and turns instantly. */
    @Test
    void settingWithoutRealismHasNoFalloffAndTurnsInstantly() {
        TurretStats stats = ConfigLogicSettings.toStats(
                new TurretSetting(true, 10, 20, 2, 50, 4, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D));
        assertEquals(TurretStats.NO_FALLOFF_OPTIMAL_RANGE, stats.optimalRange(), DELTA);
        assertEquals(TurretStats.NO_FALLOFF_DAMAGE, stats.falloffDamage(), DELTA);
        assertEquals(TurretStats.INSTANT_TURN, stats.turnRate(), DELTA);
    }

    /**
     * OMLib's config sync reads the settings with Gson; keys an older server does not send keep their defaults (the
     * private no-arg constructor runs the field initializers), so they are no falloff and instant turning, not 0.
     */
    @Test
    void aSyncedSettingWithoutTheNewKeysHasNoFalloffAndTurnsInstantly() {
        TurretSetting setting = new Gson().fromJson("{\"baseRange\": 12, \"baseDamage\": 3}", TurretSetting.class);
        assertEquals(12, setting.baseRange);
        assertEquals(TurretStats.NO_FALLOFF_OPTIMAL_RANGE, setting.optimalRange, DELTA);
        assertEquals(TurretStats.NO_FALLOFF_DAMAGE, setting.falloffDamage, DELTA);
        assertEquals(TurretStats.INSTANT_TURN, setting.turnRate, DELTA);
    }

    @Test
    void aimToleranceDefaultsToFiveDegreesAndFollowsTheConfig() {
        assertEquals(5.0D, new OMTConfig.ConfigTurrets().aimTolerance, DELTA);
        double saved = OMTConfig.TURRETS.aimTolerance;
        try {
            OMTConfig.TURRETS.aimTolerance = 12.5D;
            assertEquals(12.5D, ConfigLogicSettings.INSTANCE.aimTolerance(), DELTA);
        } finally {
            OMTConfig.TURRETS.aimTolerance = saved;
        }
    }

    @Test
    void newKeysHaveTheirRanges() throws NoSuchFieldException {
        assertRange(TurretSetting.class, "optimalRange", 0, 1);
        assertRange(TurretSetting.class, "falloffDamage", 0, 1);
        assertRange(TurretSetting.class, "turnRate", 0, 180);
        assertRange(OMTConfig.ConfigTurrets.class, "aimTolerance", 0, 180);
    }

    private static void assertRange(Class<?> owner, String field, double min, double max) throws NoSuchFieldException {
        Config.RangeDouble range = owner.getField(field).getAnnotation(Config.RangeDouble.class);
        assertNotNull(range, "No @Config.RangeDouble on " + field);
        assertEquals(min, range.min(), DELTA, "minimum of " + field);
        assertEquals(max, range.max(), DELTA, "maximum of " + field);
        assertNotNull(owner.getField(field).getAnnotation(Config.Comment.class), "No @Config.Comment on " + field);
    }
}

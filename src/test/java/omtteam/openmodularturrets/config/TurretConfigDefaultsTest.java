package omtteam.openmodularturrets.config;

import omtteam.omlib.config.Range;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The defaults of the range falloff and the turn rate (issue 539) and of the effect strength (issue 324), and their way
 * from the config to the stats.
 */
class TurretConfigDefaultsTest {
    private static final double DELTA = 1.0E-9;

    /** optimalRange, falloffDamage and turnRate of a built-in turret. */
    private record Realism(double optimalRange, double falloffDamage, double turnRate) {
    }

    /**
     * The defaults of every built-in turret. The arc, slowing, teleporter and melee turrets turn instantly, because
     * their models do not turn.
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
        map.put(TurretType.REPELLING_TURRET, new Realism(1, 1, 0));
        map.put(TurretType.TRACTOR_TURRET, new Realism(1, 1, 0));
        map.put(TurretType.ENERGY_DRAIN_TURRET, new Realism(1, 1, 0));
        return map;
    }

    @Test
    void everyBuiltInTurretHasItsRealismDefaults() {
        ConfigData.Turrets turrets = new ConfigData.Turrets();
        for (TurretType type : TurretType.values()) {
            if (!type.isBuiltIn()) continue;
            Realism expected = DEFAULTS.get(type);
            assertNotNull(expected, "No expected defaults for " + type);
            TurretSetting setting = turrets.get(type);
            assertEquals(expected.optimalRange(), setting.optimalRange, DELTA, "optimalRange of " + type);
            assertEquals(expected.falloffDamage(), setting.falloffDamage, DELTA, "falloffDamage of " + type);
            assertEquals(expected.turnRate(), setting.turnRate, DELTA, "turnRate of " + type);
            TurretStats stats = setting.toStats();
            assertEquals(expected.optimalRange(), stats.optimalRange(), DELTA, "stats optimalRange of " + type);
            assertEquals(expected.falloffDamage(), stats.falloffDamage(), DELTA, "stats falloffDamage of " + type);
            assertEquals(expected.turnRate(), stats.turnRate(), DELTA, "stats turnRate of " + type);
        }
        for (TurretType type : DEFAULTS.keySet()) {
            assertNotNull(turrets.get(type), "No config section for " + type);
        }
    }

    /** All 18 stats survive the way from an addon's default stats into the config and back. */
    @Test
    void settingRoundTripKeepsEveryStat() {
        TurretStats stats = new TurretStats(false, 21, 17, 9, 1234, 3.5D, 2, 0.07D, 0.15D, 3, 0.25D, 0.09D, 0.12D,
                0.06D, 0.4D, 0.3D, 7.5D, 2.5D);
        assertEquals(stats, TurretSetting.of(stats).toStats());
        TurretStats built = TurretStats.builder(12, 10, 4, 100).falloff(0.8D, 0.5D).turnRate(30).build();
        assertEquals(built, TurretSetting.of(built).toStats());
    }

    /** A setting from the old 14-value constructor has no falloff and turns instantly. */
    @Test
    void settingWithoutRealismHasNoFalloffAndTurnsInstantly() {
        TurretStats stats = new TurretSetting(true, 10, 20, 2, 50, 4, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D)
                .toStats();
        assertEquals(TurretStats.NO_FALLOFF_OPTIMAL_RANGE, stats.optimalRange(), DELTA);
        assertEquals(TurretStats.NO_FALLOFF_DAMAGE, stats.falloffDamage(), DELTA);
        assertEquals(TurretStats.INSTANT_TURN, stats.turnRate(), DELTA);
    }

    /** The effect strength of the turrets of issue 324 (placeholders until they are tuned); 0 for all others. */
    @Test
    void onlyTheEffectTurretsHaveAnEffectStrength() {
        ConfigData.Turrets turrets = new ConfigData.Turrets();
        Map<TurretType, Double> effects = Map.of(TurretType.REPELLING_TURRET, 0.8D, TurretType.TRACTOR_TURRET, 0.8D,
                TurretType.ENERGY_DRAIN_TURRET, 10000D);
        for (TurretType type : TurretType.values()) {
            double expected = effects.getOrDefault(type, TurretStats.NO_EFFECT);
            assertEquals(expected, turrets.get(type).effectStrength, DELTA, "effectStrength of " + type);
            assertEquals(expected, turrets.get(type).toStats().effectStrength(), DELTA, "stats effectStrength of " + type);
        }
    }

    @Test
    void settingWithoutEffectHasNone() {
        TurretSetting setting = new TurretSetting(true, 10, 20, 2, 100, 10, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D);
        assertEquals(TurretStats.NO_EFFECT, setting.toStats().effectStrength(), DELTA);
        assertEquals(1.5D, TurretSetting.of(TurretStats.builder(10, 20, 2, 100).effectStrength(1.5D).build())
                .toStats().effectStrength(), DELTA);
    }

    @Test
    void aimToleranceDefaultsToFiveDegrees() {
        assertEquals(5.0D, new ConfigData.Turrets().aimTolerance, DELTA);
    }

    @Test
    void newKeysHaveTheirRanges() throws NoSuchFieldException {
        assertRange(TurretSetting.class, "optimalRange", 0, 1);
        assertRange(TurretSetting.class, "falloffDamage", 0, 1);
        assertRange(TurretSetting.class, "turnRate", 0, 180);
        assertEquals(0, TurretSetting.class.getField("effectStrength").getAnnotation(Range.class).min(), DELTA);
        assertRange(ConfigData.Turrets.class, "aimTolerance", 0, 180);
    }

    private static void assertRange(Class<?> owner, String field, double min, double max) throws NoSuchFieldException {
        Range range = owner.getField(field).getAnnotation(Range.class);
        assertNotNull(range, "No @Range on " + field);
        assertEquals(min, range.min(), DELTA, "minimum of " + field);
        assertEquals(max, range.max(), DELTA, "maximum of " + field);
    }
}

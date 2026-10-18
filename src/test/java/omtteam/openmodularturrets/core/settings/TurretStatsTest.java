package omtteam.openmodularturrets.core.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** {@link TurretStats.Builder}: the default stats of turrets added through the turret API. */
class TurretStatsTest {
    @Test
    void builderStartsAtTheCommonDefaults() {
        TurretStats stats = TurretStats.builder(24, 20, 6, 500).build();
        assertEquals(new TurretStats(true, 24, 20, 6, 500, 10, 4, 0.05D, 0.1D, 2, 0.2D, 0.08D, 0.1D, 0.05D), stats);
    }

    @Test
    void builderSetsEveryValue() {
        TurretStats stats = TurretStats.builder(24, 20, 6, 500)
                .enabled(false)
                .accuracyDeviation(15)
                .maxSimultaneous(2)
                .damageAmp(0.07D)
                .fireRateUpgrade(0.15D)
                .rangeUpgrade(3)
                .accuracyUpgrade(0.25D)
                .efficiencyUpgrade(0.1D)
                .recycler(0.2D, 0.3D)
                .build();
        assertEquals(new TurretStats(false, 24, 20, 6, 500, 15, 2, 0.07D, 0.15D, 3, 0.25D, 0.1D, 0.2D, 0.3D), stats);
    }
}

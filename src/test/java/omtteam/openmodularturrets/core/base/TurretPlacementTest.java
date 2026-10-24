package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.base.TurretPlacement.Result;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretPlacementTest {
    private final FakeSettings settings = new FakeSettings();

    /** Sets the type's {@code maxSimultaneous}, keeping the rest of the rocket turret stats. */
    private void limit(TurretType type, int maxSimultaneous) {
        settings.turrets.put(type, FakeSettings.stats(30, 30, 10, 5000, 10, maxSimultaneous, 0.08D, 0.1D, 2, 0.2D,
                0.08D, 0.1D));
    }

    private void disable(TurretType type) {
        TurretStats s = settings.turret(type);
        settings.turrets.put(type, new TurretStats(false, s.baseRange(), s.baseFireRate(), s.baseDamage(), s.powerUsage(),
                s.baseAccuracyDeviation(), s.maxSimultaneous(), s.damageAmp(), s.fireRateUpgrade(), s.rangeUpgrade(),
                s.accuracyUpgrade(), s.efficiencyUpgrade(), s.recyclerNegateChance(), s.recyclerAddChance()));
    }

    @Test
    void onlyOkIsAllowed() {
        for (Result result : Result.values()) {
            assertEquals(result == Result.OK, result.allowed(), result.name());
        }
    }

    @Nested
    class CanAttach {
        @Test
        void freeBase() {
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 4, Map.of(), settings));
        }

        @Test
        void disabledType() {
            disable(TurretType.MACHINE_GUN_TURRET);
            assertEquals(Result.DISABLED,
                    TurretPlacement.canAttach(TurretType.MACHINE_GUN_TURRET, Face.UP, 5, Map.of(), settings));
        }

        @Test
        void wrongFace() {
            settings.turrets.put(TurretType.MELEE_TURRET, settings.turret(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Result.WRONG_FACE,
                    TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.NORTH, 5, Map.of(), settings));
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.DOWN, 5, Map.of(), settings));
        }

        @Test
        void tierTooLow() {
            assertEquals(Result.TIER_TOO_LOW,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 3, Map.of(), settings));
        }

        @Test
        void baseFull() {
            // tier 3 holds 2 turrets
            Map<Face, TurretType> attached = Map.of(Face.UP, TurretType.ROCKET_TURRET, Face.DOWN, TurretType.LASER_TURRET);
            assertEquals(Result.BASE_FULL,
                    TurretPlacement.canAttach(TurretType.MACHINE_GUN_TURRET, Face.NORTH, 3, attached, settings));
        }

        @Test
        void typeLimit() {
            limit(TurretType.ROCKET_TURRET, 1);
            Map<Face, TurretType> attached = Map.of(Face.UP, TurretType.ROCKET_TURRET);
            assertEquals(Result.TYPE_LIMIT,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.NORTH, 5, attached, settings));
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.LASER_TURRET, Face.NORTH, 5, attached, settings));
        }

        @Test
        void ownFaceIsNotCounted() {
            limit(TurretType.ROCKET_TURRET, 1);
            Map<Face, TurretType> attached = Map.of(Face.UP, TurretType.ROCKET_TURRET);
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 5, attached, settings));
            // tier 1 holds 1 turret
            assertEquals(Result.OK,
                    TurretPlacement.canAttach(TurretType.DISPOSABLE_TURRET, Face.UP, 1, attached, settings));
        }

        @Test
        void firstFailingCheckWins() {
            disable(TurretType.ROCKET_TURRET);
            assertEquals(Result.DISABLED,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 1, Map.of(), settings));
            settings.turrets.put(TurretType.MELEE_TURRET, settings.turret(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Result.WRONG_FACE,
                    TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.NORTH, 0, Map.of(), settings));
            // tier 1 holds 1 turret, and the type's limit is reached as well
            limit(TurretType.DISPOSABLE_TURRET, 1);
            Map<Face, TurretType> full = Map.of(Face.UP, TurretType.DISPOSABLE_TURRET);
            assertEquals(Result.BASE_FULL,
                    TurretPlacement.canAttach(TurretType.DISPOSABLE_TURRET, Face.DOWN, 1, full, settings));
        }
    }

    @Nested
    class CanStay {
        private final Map<Face, TurretType> threeRockets = Map.of(Face.DOWN, TurretType.ROCKET_TURRET,
                Face.UP, TurretType.ROCKET_TURRET, Face.NORTH, TurretType.ROCKET_TURRET);

        @Test
        void wrongFaceAndTierDrop() {
            settings.turrets.put(TurretType.MELEE_TURRET, settings.turret(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Result.WRONG_FACE,
                    TurretPlacement.canStay(TurretType.MELEE_TURRET, Face.EAST, 5, Map.of(), settings));
            assertEquals(Result.TIER_TOO_LOW,
                    TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.UP, 3, Map.of(), settings));
        }

        @Test
        void limitsAndDisablingKeepTurretsByDefault() {
            limit(TurretType.ROCKET_TURRET, 1);
            disable(TurretType.ROCKET_TURRET);
            for (Face face : threeRockets.keySet()) {
                // tier 4 holds 3 turrets; the type is disabled and over its limit
                assertTrue(TurretPlacement.canStay(TurretType.ROCKET_TURRET, face, 4, threeRockets, settings).allowed());
            }
        }

        @Test
        void overTheLimitDropsTheLastFacesWhenEnabled() {
            settings.dropOverMaxSimultaneous = true;
            limit(TurretType.ROCKET_TURRET, 2);
            assertEquals(Result.OK, TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.DOWN, 5, threeRockets, settings));
            assertEquals(Result.OK, TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.UP, 5, threeRockets, settings));
            assertEquals(Result.TYPE_LIMIT,
                    TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.NORTH, 5, threeRockets, settings));
        }

        @Test
        void otherTypesDoNotCountTowardsTheLimit() {
            settings.dropOverMaxSimultaneous = true;
            limit(TurretType.ROCKET_TURRET, 1);
            Map<Face, TurretType> attached = Map.of(Face.DOWN, TurretType.LASER_TURRET, Face.UP, TurretType.ROCKET_TURRET);
            assertFalse(TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.UP, 5,
                    Map.of(Face.DOWN, TurretType.ROCKET_TURRET, Face.UP, TurretType.ROCKET_TURRET), settings).allowed());
            assertTrue(TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.UP, 5, attached, settings).allowed());
        }
    }
}

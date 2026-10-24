package omtteam.openmodularturrets.core.base;

import omtteam.openmodularturrets.core.base.TurretPlacement.Result;
import omtteam.openmodularturrets.core.settings.TurretStats;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private static Map<Face, TurretType> none() {
        return Collections.emptyMap();
    }

    /** Turrets by face: alternating {@link Face} and {@link TurretType}. */
    private static Map<Face, TurretType> attached(Object... facesAndTypes) {
        Map<Face, TurretType> map = new EnumMap<>(Face.class);
        for (int i = 0; i < facesAndTypes.length; i += 2) {
            map.put((Face) facesAndTypes[i], (TurretType) facesAndTypes[i + 1]);
        }
        return map;
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
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 4, none(), settings));
        }

        @Test
        void disabledType() {
            disable(TurretType.MACHINE_GUN_TURRET);
            assertEquals(Result.DISABLED,
                    TurretPlacement.canAttach(TurretType.MACHINE_GUN_TURRET, Face.UP, 5, none(), settings));
        }

        @Test
        void wrongFace() {
            settings.turrets.put(TurretType.MELEE_TURRET, settings.turret(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Result.WRONG_FACE,
                    TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.NORTH, 5, none(), settings));
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.DOWN, 5, none(), settings));
        }

        @Test
        void tierTooLow() {
            assertEquals(Result.TIER_TOO_LOW,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 3, none(), settings));
        }

        @Test
        void baseFull() {
            // tier 3 holds 2 turrets
            Map<Face, TurretType> full = attached(Face.UP, TurretType.ROCKET_TURRET, Face.DOWN, TurretType.LASER_TURRET);
            assertEquals(Result.BASE_FULL,
                    TurretPlacement.canAttach(TurretType.MACHINE_GUN_TURRET, Face.NORTH, 3, full, settings));
        }

        @Test
        void typeLimit() {
            limit(TurretType.ROCKET_TURRET, 1);
            Map<Face, TurretType> rocket = attached(Face.UP, TurretType.ROCKET_TURRET);
            assertEquals(Result.TYPE_LIMIT,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.NORTH, 5, rocket, settings));
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.LASER_TURRET, Face.NORTH, 5, rocket, settings));
        }

        @Test
        void ownFaceIsNotCounted() {
            limit(TurretType.ROCKET_TURRET, 1);
            Map<Face, TurretType> rocket = attached(Face.UP, TurretType.ROCKET_TURRET);
            assertEquals(Result.OK, TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 5, rocket, settings));
            // tier 1 holds 1 turret
            assertEquals(Result.OK,
                    TurretPlacement.canAttach(TurretType.DISPOSABLE_TURRET, Face.UP, 1, rocket, settings));
        }

        @Test
        void firstFailingCheckWins() {
            disable(TurretType.ROCKET_TURRET);
            assertEquals(Result.DISABLED,
                    TurretPlacement.canAttach(TurretType.ROCKET_TURRET, Face.UP, 1, none(), settings));
            settings.turrets.put(TurretType.MELEE_TURRET, settings.turret(TurretType.MACHINE_GUN_TURRET));
            assertEquals(Result.WRONG_FACE,
                    TurretPlacement.canAttach(TurretType.MELEE_TURRET, Face.NORTH, 0, none(), settings));
            // tier 1 holds 1 turret, and the type's limit is reached as well
            limit(TurretType.DISPOSABLE_TURRET, 1);
            Map<Face, TurretType> full = attached(Face.UP, TurretType.DISPOSABLE_TURRET);
            assertEquals(Result.BASE_FULL,
                    TurretPlacement.canAttach(TurretType.DISPOSABLE_TURRET, Face.DOWN, 1, full, settings));
        }
    }

    @Nested
    class CanStay {
        @Test
        void wrongFaceAndTierDrop() {
            assertEquals(Result.WRONG_FACE, TurretPlacement.canStay(TurretType.MELEE_TURRET, Face.EAST, 5));
            assertEquals(Result.TIER_TOO_LOW, TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.UP, 3));
        }

        @Test
        void sufficientTierOnAnAllowedFaceStays() {
            assertEquals(Result.OK, TurretPlacement.canStay(TurretType.MELEE_TURRET, Face.UP, 1));
            assertEquals(Result.OK, TurretPlacement.canStay(TurretType.ROCKET_TURRET, Face.EAST, 4));
        }
    }
}

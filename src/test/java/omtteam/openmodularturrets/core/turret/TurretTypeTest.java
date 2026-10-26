package omtteam.openmodularturrets.core.turret;

import omtteam.omlib.core.world.Face;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretTypeTest {
    @Test
    void ammoMatchesOriginalTurrets() {
        assertEquals(Optional.of(AmmoType.DISPOSABLE), TurretType.DISPOSABLE_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.POTATO), TurretType.POTATO_CANNON_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.BOLT), TurretType.CROSSBOW_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.BULLET), TurretType.MACHINE_GUN_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.BLAZING_CLAY), TurretType.INCENDIARY_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.GRENADE), TurretType.GRENADE_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.ROCKET), TurretType.ROCKET_TURRET.getAmmo());
        assertEquals(Optional.of(AmmoType.FERRO_SLUG), TurretType.RAILGUN_TURRET.getAmmo());
    }

    @Test
    void energyTurretsNeedNoAmmo() {
        Set<TurretType> energyOnly = Set.of(TurretType.MELEE_TURRET, TurretType.SLOWING_TURRET,
                TurretType.TELEPORTER_TURRET, TurretType.ARC_TURRET, TurretType.LASER_TURRET, TurretType.PLASMA_TURRET);
        for (TurretType type : TurretType.BUILT_IN) {
            assertEquals(energyOnly.contains(type), type.getAmmo().isEmpty(), type.getId());
        }
    }

    @Test
    void meleeTurretMountsOnlyOnTopOrBottom() {
        for (Face face : Face.values()) {
            boolean vertical = face == Face.UP || face == Face.DOWN;
            assertEquals(vertical, TurretType.MELEE_TURRET.canMountOn(face), face.name());
            assertTrue(TurretType.MACHINE_GUN_TURRET.canMountOn(face), face.name());
        }
    }

    @Test
    void requiredTiersMatchOriginal() {
        assertEquals(1, TurretType.DISPOSABLE_TURRET.getRequiredBaseTier());
        assertEquals(2, TurretType.MACHINE_GUN_TURRET.getRequiredBaseTier());
        assertEquals(3, TurretType.GRENADE_TURRET.getRequiredBaseTier());
        assertEquals(4, TurretType.ARC_TURRET.getRequiredBaseTier());
        assertEquals(5, TurretType.PLASMA_TURRET.getRequiredBaseTier());
    }

    @Test
    void scattershotIsIgnoredByRailgunAndEffectTurrets() {
        Set<TurretType> ignoring = Set.of(TurretType.RAILGUN_TURRET, TurretType.MELEE_TURRET,
                TurretType.SLOWING_TURRET, TurretType.TELEPORTER_TURRET);
        for (TurretType type : TurretType.BUILT_IN) {
            assertEquals(!ignoring.contains(type), type.usesScattershot(), type.getId());
        }
    }

    @Test
    void onlyTheSlowingTurretSkipsSlowedTargets() {
        for (TurretType type : TurretType.BUILT_IN) {
            assertEquals(type == TurretType.SLOWING_TURRET, type.skipsSlowedTargets(), type.getId());
        }
    }

    @Test
    void idsRoundTrip() {
        assertEquals(14, TurretType.BUILT_IN.size());
        for (TurretType type : TurretType.BUILT_IN) {
            assertEquals(Optional.of(type), TurretType.byId(type.getId()));
            assertEquals(Optional.of(type), TurretType.byId(type.getPath()), "bare paths are built-in ids");
            assertTrue(type.isBuiltIn());
            assertEquals("openmodularturrets:" + type.getPath(), type.getId());
            assertTrue(type.getPath().endsWith("_turret"));
        }
    }

    @Test
    void builderDefaults() {
        TurretType type = TurretType.builder("othermod", "test_turret").build();
        assertEquals("othermod:test_turret", type.getId());
        assertEquals("othermod", type.getNamespace());
        assertFalse(type.isBuiltIn());
        assertEquals(1, type.getRequiredBaseTier());
        assertTrue(type.getAmmo().isEmpty());
        assertTrue(type.usesScattershot());
        assertFalse(type.skipsSlowedTargets());
        for (Face face : Face.values()) {
            assertTrue(type.canMountOn(face));
        }
    }

    @Test
    void builderSetsEveryOption() {
        AmmoType shell = AmmoType.of("othermod", "shell");
        TurretType type = TurretType.builder("othermod", "options_turret")
                .requiredBaseTier(5)
                .ammo(shell)
                .priorities(1, 2, 3, 4, 5)
                .mountFaces(Face.UP)
                .ignoresScattershot()
                .skipsSlowedTargets()
                .build();
        assertEquals(5, type.getRequiredBaseTier());
        assertEquals(Optional.of(shell), type.getAmmo());
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, type.getDefaultPriorities());
        assertTrue(type.canMountOn(Face.UP));
        assertFalse(type.canMountOn(Face.DOWN));
        assertFalse(type.usesScattershot());
        assertTrue(type.skipsSlowedTargets());
        assertEquals(1, TurretType.builder("othermod", "t").requiredBaseTier(1).build().getRequiredBaseTier(),
                "tier 1 is the lowest allowed tier");
    }

    @Test
    void builderRejectsInvalidValues() {
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("Bad", "turret"));
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("mod", "with space"));
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("mod", "t").requiredBaseTier(0));
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("mod", "t").requiredBaseTier(6));
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("mod", "t").priorities(1, 2, 3));
        assertThrows(IllegalArgumentException.class, () -> TurretType.builder("mod", "t").mountFaces());
    }

    @Test
    void registeredTypesFollowTheBuiltInOnes() {
        assertFalse(TurretTypes.isFrozen(), "the registry is only frozen while the game starts");
        TurretType added = TurretTypes.register(TurretType.builder("othermod", "registered_turret").build());
        assertEquals(TurretType.BUILT_IN, TurretTypes.all().subList(0, TurretType.BUILT_IN.size()));
        assertTrue(TurretTypes.all().contains(added));
        assertEquals(TurretTypes.all(), TurretType.values());
        assertEquals(Optional.of(added), TurretTypes.byId("othermod:registered_turret"));
        assertEquals(Optional.empty(), TurretTypes.byId("registered_turret"), "bare paths are built-in ids");
        assertThrows(IllegalArgumentException.class,
                () -> TurretTypes.register(TurretType.builder("othermod", "registered_turret").build()));
        assertThrows(IllegalArgumentException.class,
                () -> TurretTypes.register(TurretType.builder("openmodularturrets", "laser_turret").build()));
        assertThrows(UnsupportedOperationException.class, () -> TurretTypes.all().clear());
    }

    @Test
    void ammoTypesAreValues() {
        assertEquals(AmmoType.BULLET, AmmoType.of("openmodularturrets", "bullet"));
        assertEquals("othermod:shell", AmmoType.of("othermod", "shell").getId());
        assertThrows(IllegalArgumentException.class, () -> AmmoType.of("othermod", "Shell"));
    }
}

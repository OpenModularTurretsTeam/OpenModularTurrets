package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Face;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Set<TurretType> energyOnly = EnumSet.of(TurretType.MELEE_TURRET, TurretType.SLOWING_TURRET,
                TurretType.TELEPORTER_TURRET, TurretType.ARC_TURRET, TurretType.LASER_TURRET, TurretType.PLASMA_TURRET);
        for (TurretType type : TurretType.values()) {
            assertEquals(energyOnly.contains(type), type.getAmmo().isEmpty(), type.name());
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
    void idsRoundTrip() {
        for (TurretType type : TurretType.values()) {
            assertEquals(Optional.of(type), TurretType.byId(type.getId()));
            assertTrue(type.getId().endsWith("_turret"));
        }
    }
}

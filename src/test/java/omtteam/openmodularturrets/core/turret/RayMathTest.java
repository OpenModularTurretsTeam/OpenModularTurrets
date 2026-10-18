package omtteam.openmodularturrets.core.turret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RayMathTest {
    private static final double EPS = 1e-5;

    @Test
    void laserIsWeakerAgainstArmor() {
        assertEquals(1.6, RayMath.damageModifier(TurretType.LASER_TURRET, 0), EPS);
        assertEquals(1.1, RayMath.damageModifier(TurretType.LASER_TURRET, 10), EPS);
        assertEquals(0.6, RayMath.damageModifier(TurretType.LASER_TURRET, 20), EPS);
    }

    @Test
    void railgunIsStrongerAgainstArmor() {
        assertEquals(0.6, RayMath.damageModifier(TurretType.RAILGUN_TURRET, 0), EPS);
        assertEquals(0.7, RayMath.damageModifier(TurretType.RAILGUN_TURRET, 2), EPS);
        assertEquals(1.6, RayMath.damageModifier(TurretType.RAILGUN_TURRET, 20), EPS);
    }

    @Test
    void arcIgnoresArmor() {
        assertEquals(1, RayMath.damageModifier(TurretType.ARC_TURRET, 0), EPS);
        assertEquals(1, RayMath.damageModifier(TurretType.ARC_TURRET, 20), EPS);
    }

    @Test
    void onlyRayTurretsHaveRayRules() {
        assertThrows(IllegalArgumentException.class, () -> RayMath.damageModifier(TurretType.MACHINE_GUN_TURRET, 0));
        assertThrows(IllegalArgumentException.class, () -> RayMath.split(TurretType.PLASMA_TURRET, 10F));
    }

    @Test
    void damageIsBaseDamageTimesModifier() {
        // the railgun game test: 25 base damage against a husk (2 armor)
        double modifier = RayMath.damageModifier(TurretType.RAILGUN_TURRET, 2);
        assertEquals(17.5, RayMath.damage(25, modifier, 0, 20F), EPS);
    }

    @Test
    void damageAmpAddsAShareOfTheHealth() {
        // health rounded down first: (int) 25.7 * 0.1 = 2.5, not rounded afterwards for rays
        assertEquals(12.5, RayMath.damage(10, 1, 0.1, 25.7F), EPS);
        assertEquals(10, RayMath.damage(10, 1, 0, 25.7F), EPS);
        assertEquals(10, RayMath.damage(10, 1, 0.1, 0F), EPS, "targets without health get no bonus");
    }

    @Test
    void raysAndProjectilesShareTheAmpBonus() {
        double bonus = ProjectileMath.ampBonus(25.7F, 0.05 * 2);
        assertEquals(2.5, bonus, EPS);
        assertEquals(10 + bonus, RayMath.damage(10, 1, 0.05 * 2, 25.7F), EPS);
        // projectiles round the bonus down to whole points
        assertEquals(12, ProjectileMath.damageWithAmp(10, 2, 0.05, 25.7F));
    }

    @Test
    void splits() {
        assertEquals(new ProjectileMath.Split(10F, 0F), RayMath.split(TurretType.LASER_TURRET, 10F));
        assertEquals(new ProjectileMath.Split(0F, 10F), RayMath.split(TurretType.RAILGUN_TURRET, 10F));
        ProjectileMath.Split arc = RayMath.split(TurretType.ARC_TURRET, 10F);
        assertEquals(6F, arc.normal(), EPS);
        assertEquals(4F, arc.armorBypass(), EPS);
    }

    @Test
    void arcJumps() {
        assertEquals(6, RayMath.arcJumpRange(RayMath.ARC_HOPS), EPS);
        assertEquals(3, RayMath.arcJumpRange(1), EPS);
        assertEquals(1D / 3D, RayMath.ARC_JUMP_FACTOR, EPS);
    }
}

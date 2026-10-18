package omtteam.openmodularturrets.core.turret;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RayMathTest {
    private static final double EPS = 1e-5;

    @Test
    void laserIsWeakerAgainstArmor() {
        assertEquals(1.6, RayMath.damageModifier(RayProfile.LASER, 0), EPS);
        assertEquals(1.1, RayMath.damageModifier(RayProfile.LASER, 10), EPS);
        assertEquals(0.6, RayMath.damageModifier(RayProfile.LASER, 20), EPS);
    }

    @Test
    void railgunIsStrongerAgainstArmor() {
        assertEquals(0.6, RayMath.damageModifier(RayProfile.RAILGUN, 0), EPS);
        assertEquals(0.7, RayMath.damageModifier(RayProfile.RAILGUN, 2), EPS);
        assertEquals(1.6, RayMath.damageModifier(RayProfile.RAILGUN, 20), EPS);
    }

    @Test
    void arcIgnoresArmor() {
        assertEquals(1, RayMath.damageModifier(RayProfile.ARC, 0), EPS);
        assertEquals(1, RayMath.damageModifier(RayProfile.ARC, 20), EPS);
    }

    @Test
    void onlyTheArcChains() {
        assertTrue(RayProfile.ARC.arcChain());
        assertFalse(RayProfile.LASER.arcChain());
        assertFalse(RayProfile.RAILGUN.arcChain());
    }

    @Test
    void damageIsBaseDamageTimesModifier() {
        // the railgun game test: 25 base damage against a husk (2 armor)
        double modifier = RayMath.damageModifier(RayProfile.RAILGUN, 2);
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
        assertEquals(new ProjectileMath.Split(10F, 0F), RayMath.split(RayProfile.LASER, 10F));
        assertEquals(new ProjectileMath.Split(0F, 10F), RayMath.split(RayProfile.RAILGUN, 10F));
        ProjectileMath.Split arc = RayMath.split(RayProfile.ARC, 10F);
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

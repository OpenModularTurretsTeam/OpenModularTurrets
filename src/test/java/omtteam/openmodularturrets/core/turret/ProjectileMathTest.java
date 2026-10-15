package omtteam.openmodularturrets.core.turret;

import static org.junit.jupiter.api.Assertions.assertEquals;

import omtteam.openmodularturrets.core.world.Vector3;
import org.junit.jupiter.api.Test;

class ProjectileMathTest {
    private static final double EPS = 1.0E-9;

    @Test
    void ampAddsShareOfHealthRoundedDown() {
        // (int) 20.7 = 20; 20 * (0.05 * 3) = 3.0000000000000004 -> 3
        assertEquals(13, ProjectileMath.damageWithAmp(10, 3, 0.05D, 20.7F));
        // 7 * 0.5 = 3.5 -> 3
        assertEquals(13, ProjectileMath.damageWithAmp(10, 1, 0.5D, 7F));
    }

    @Test
    void noAmpLeavesDamageUnchanged() {
        assertEquals(10, ProjectileMath.damageWithAmp(10, 0, 0.5D, 100F));
    }

    @Test
    void splits() {
        ProjectileMath.Split grenade = ProjectileMath.grenadeSplit(10);
        assertEquals(9.0F, grenade.normal(), 1.0E-6F);
        assertEquals(1.0F, grenade.armorBypass(), 1.0E-6F);
        ProjectileMath.Split plasma = ProjectileMath.plasmaSplit(7);
        assertEquals(3.5F, plasma.normal());
        assertEquals(3.5F, plasma.armorBypass());
    }

    @Test
    void motionHasDragAndGravity() {
        Vector3 air = ProjectileMath.nextMotion(new Vector3(1, 2, 3), false, false, 0.03D);
        assertEquals(0.99, air.x(), EPS);
        assertEquals(1.98 - 0.03, air.y(), EPS);
        assertEquals(2.97, air.z(), EPS);
        Vector3 water = ProjectileMath.nextMotion(new Vector3(1, 2, 3), true, true, 0.03D);
        assertEquals(0.8, water.x(), EPS);
        assertEquals(1.6, water.y(), EPS);
    }

    @Test
    void inaccuracyIsAccuracyDeviationOverFive() {
        assertEquals(2.0F, ProjectileMath.inaccuracy(10D));
    }

    @Test
    void raySpreadGrowsWithDistanceAndSmallTargets() {
        double near = ProjectileMath.raySpread(10D, 1.8D, 0D, 10D);
        double far = ProjectileMath.raySpread(10D, 1.8D, 10D, 10D);
        double small = ProjectileMath.raySpread(10D, 0.3D, 0D, 10D);
        assertEquals(0.003D * 10D * 0.3D * 10D, near, EPS);
        assertEquals(0.003D * 10D * 0.5D * 10D, far, EPS);
        assertEquals(near * 1.5D, small, EPS);
    }
}

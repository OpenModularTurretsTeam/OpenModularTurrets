package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurretMathTest {
    private static final double EPS = 1e-6;

    private static void assertVector(Vector3 expected, Vector3 actual) {
        assertEquals(expected.x(), actual.x(), EPS, "x");
        assertEquals(expected.y(), actual.y(), EPS, "y");
        assertEquals(expected.z(), actual.z(), EPS, "z");
    }

    @ParameterizedTest
    @EnumSource(Face.class)
    void framesAreRightHandedAndOrthonormal(Face face) {
        TurretMath.Frame frame = TurretMath.frame(face);
        assertVector(Vector3.of(face), frame.up());
        assertEquals(1, frame.right().length(), EPS);
        assertEquals(1, frame.forward().length(), EPS);
        assertEquals(0, frame.right().dot(frame.up()), EPS);
        assertEquals(0, frame.forward().dot(frame.up()), EPS);
        assertVector(frame.forward(), frame.right().cross(frame.up()));
    }

    @ParameterizedTest
    @EnumSource(Face.class)
    void anglesAndDirectionRoundTrip(Face face) {
        Vector3 target = new Vector3(3, 2, -5);
        TurretMath.Angles angles = TurretMath.anglesFor(face, target);
        assertVector(target.scale(1 / target.length()), TurretMath.direction(face, angles));
    }

    @Test
    void straightAwayFromTheBaseIsPitch90() {
        assertEquals(90, TurretMath.anglesFor(Face.UP, new Vector3(0, 5, 0)).pitch(), EPS);
        assertEquals(90, TurretMath.anglesFor(Face.NORTH, new Vector3(0, 0, -5)).pitch(), EPS);
        assertEquals(-90, TurretMath.anglesFor(Face.DOWN, new Vector3(0, 5, 0)).pitch(), EPS, "into the base");
    }

    @Test
    void turretsCannotAimIntoTheirBase() {
        assertTrue(TurretMath.canAim(Face.UP, new Vector3(5, -1, 0)), "slightly below the mounting plane");
        assertFalse(TurretMath.canAim(Face.UP, new Vector3(1, -5, 0)));
        assertFalse(TurretMath.canAim(Face.EAST, new Vector3(-5, 0, 0)), "straight through the base");
        assertEquals(TurretMath.MIN_PITCH, TurretMath.limit(new TurretMath.Angles(30, -80)).pitch(), EPS);
        assertEquals(30, TurretMath.limit(new TurretMath.Angles(30, -80)).yaw(), EPS);
    }

    @Test
    void yawOnTopOfABase() {
        assertEquals(0, TurretMath.anglesFor(Face.UP, new Vector3(0, 0, 1)).yaw(), EPS);
        assertEquals(90, TurretMath.anglesFor(Face.UP, new Vector3(1, 0, 0)).yaw(), EPS, "right of forward");
        assertEquals(-90, TurretMath.anglesFor(Face.UP, new Vector3(-1, 0, 0)).yaw(), EPS);
        assertEquals(0, TurretMath.anglesFor(Face.UP, new Vector3(0, 0, 1)).pitch(), EPS);
    }

    @Test
    void leadFollowsTheTarget() {
        // target 6 blocks away moving 0.5 blocks/tick sideways; flight time 6 / 3 = 2 ticks
        TurretMath.Lead lead = TurretMath.lead(new Vector3(0, 0, 6), new Vector3(0.5, 0, 0), false);
        assertVector(new Vector3(1, -0.1, 6), lead.direction());
        assertEquals(Math.sqrt(37) / 6, lead.speedFactor(), 1e-5);
    }

    @Test
    void gravityProjectilesLeadMore() {
        TurretMath.Lead lead = TurretMath.lead(new Vector3(0, 0, 8), new Vector3(0.4, 0, 0), true);
        assertEquals(2.0, lead.direction().x(), EPS); // 8 / 1.6 = 5 ticks
    }

    @Test
    void stationaryTargetsNeedNoCorrection() {
        TurretMath.Lead lead = TurretMath.lead(new Vector3(2, 3, 6), Vector3.ZERO, false);
        assertEquals(1.0F, lead.speedFactor(), 1e-6);
    }

    @Test
    void angleDifferenceTakesTheShortWay() {
        assertEquals(20, TurretMath.angleDifference(350, 10), EPS);
        assertEquals(-20, TurretMath.angleDifference(10, 350), EPS);
        assertEquals(-180, TurretMath.angleDifference(0, 180), EPS);
    }
}

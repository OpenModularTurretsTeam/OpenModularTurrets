package omtteam.openmodularturrets.core.world;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PoseTest {
    private static final double EPS = 1e-9;
    /** 90° around +Y (right-handed: +X turns to −Z). */
    private static final Quaternion QUARTER_TURN_Y = axisAngle(new Vector3(0, 1, 0), Math.PI / 2);
    /** An arbitrary tilt: 35° around a skewed axis. */
    private static final Quaternion TILT = axisAngle(new Vector3(1, 2, -0.5), Math.toRadians(35));

    private static Quaternion axisAngle(Vector3 axis, double angle) {
        Vector3 unit = axis.scale(1 / axis.length());
        double sin = Math.sin(angle / 2);
        return new Quaternion(unit.x() * sin, unit.y() * sin, unit.z() * sin, Math.cos(angle / 2));
    }

    private static void assertVector(Vector3 expected, Vector3 actual) {
        assertEquals(expected.x(), actual.x(), EPS, "x");
        assertEquals(expected.y(), actual.y(), EPS, "y");
        assertEquals(expected.z(), actual.z(), EPS, "z");
    }

    @Test
    void quaternionRotatesRightHanded() {
        assertVector(new Vector3(0, 0, -1), QUARTER_TURN_Y.rotate(new Vector3(1, 0, 0)));
        assertVector(new Vector3(1, 0, 0), QUARTER_TURN_Y.rotate(new Vector3(0, 0, 1)));
        assertVector(new Vector3(0, 1, 0), QUARTER_TURN_Y.rotate(new Vector3(0, 1, 0)));
    }

    @Test
    void inverseRotateUndoesRotate() {
        Vector3 v = new Vector3(3, -2, 7);
        assertVector(v, TILT.inverseRotate(TILT.rotate(v)));
        assertVector(v, TILT.rotate(TILT.inverseRotate(v)));
        assertEquals(v.length(), TILT.rotate(v).length(), EPS);
    }

    @Test
    void identityChangesNothing() {
        Vector3 v = new Vector3(12.5, -60, 3_000_000.25);
        assertVector(v, Pose.IDENTITY.toWorld(v));
        assertVector(v, Pose.IDENTITY.toLocal(v));
        assertVector(v, Pose.IDENTITY.directionToWorld(v));
        assertVector(v, Pose.IDENTITY.directionToLocal(v));
        assertTrue(Pose.IDENTITY.isIdentity());
    }

    /** A plot block far away, shown turned by 90° next to the origin. */
    @Test
    void toWorldRotatesAroundTheRotationPointAndMovesIt() {
        Pose pose = new Pose(new Vector3(10, 64, 10), QUARTER_TURN_Y, new Vector3(20_000_000, 64, 20_000_000),
                new Vector3(1, 1, 1));
        assertVector(new Vector3(10, 64, 10), pose.toWorld(new Vector3(20_000_000, 64, 20_000_000)));
        // one block east of the rotation point in the plot is one block north (−Z) of it in the world
        assertVector(new Vector3(10, 64, 9), pose.toWorld(new Vector3(20_000_001, 64, 20_000_000)));
        assertFalse(pose.isIdentity());
    }

    @Test
    void scaleStretchesAroundTheRotationPoint() {
        Pose pose = new Pose(new Vector3(0, 0, 0), new Quaternion(0, 0, 0, 1), new Vector3(5, 5, 5),
                new Vector3(2, 1, 0.5));
        assertVector(new Vector3(2, 1, 0.5), pose.toWorld(new Vector3(6, 6, 6)));
        assertVector(new Vector3(2, 1, 0.5), pose.directionToWorld(new Vector3(1, 1, 1)));
    }

    @Test
    void toLocalUndoesToWorld() {
        Pose pose = new Pose(new Vector3(-4, 70.5, 123), TILT, new Vector3(1_000_008, 80, -1_000_008),
                new Vector3(1.5, 0.75, 2));
        Vector3 local = new Vector3(1_000_010.25, 81, -1_000_003);
        assertVector(local, pose.toLocal(pose.toWorld(local)));
        Vector3 world = new Vector3(3, 65, 120);
        assertVector(world, pose.toWorld(pose.toLocal(world)));
    }

    @Test
    void directionsIgnoreThePosition() {
        Pose pose = new Pose(new Vector3(-4, 70.5, 123), TILT, new Vector3(1_000_008, 80, -1_000_008),
                new Vector3(1.5, 0.75, 2));
        Vector3 direction = new Vector3(0.3, -1, 2);
        Vector3 from = new Vector3(1_000_009, 81, -1_000_007);
        // the direction between two points maps like the points themselves
        assertVector(pose.toWorld(from.add(direction)).subtract(pose.toWorld(from)), pose.directionToWorld(direction));
        assertVector(direction, pose.directionToLocal(pose.directionToWorld(direction)));
    }
}

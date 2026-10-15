package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the ring against its own constants ({@link DamageAmpRing#RADIUS}, {@link DamageAmpRing#WAVES},
 * {@link DamageAmpRing#AMPLITUDE}, {@link DamageAmpRing#REVOLUTION_TICKS}), so tuning them does not break the tests.
 */
class DamageAmpRingTest {
    private static final double EPS = 1e-9;
    private static final double FULL_TURN = 2 * Math.PI;

    @Test
    void ringClosesWithoutASeam() {
        Vector3 start = DamageAmpRing.point(0);
        Vector3 end = DamageAmpRing.point(FULL_TURN);
        assertEquals(start.x(), end.x(), EPS);
        assertEquals(start.y(), end.y(), EPS);
        assertEquals(start.z(), end.z(), EPS);
        // the slope matches too, so the wave continues smoothly over the join
        double slopeStart = DamageAmpRing.height(1e-6) - DamageAmpRing.height(0);
        double slopeEnd = DamageAmpRing.height(FULL_TURN + 1e-6) - DamageAmpRing.height(FULL_TURN);
        assertEquals(slopeStart, slopeEnd, EPS);
    }

    @Test
    void hasAsManyHighsAndLowsAsWaves() {
        int highs = 0;
        int lows = 0;
        int steps = 3600 * DamageAmpRing.WAVES;
        for (int i = 0; i < steps; i++) {
            double before = DamageAmpRing.height(FULL_TURN * (i - 1) / steps);
            double now = DamageAmpRing.height(FULL_TURN * i / steps);
            double after = DamageAmpRing.height(FULL_TURN * (i + 1) / steps);
            if (now > before && now >= after) highs++;
            if (now < before && now <= after) lows++;
        }
        assertEquals(DamageAmpRing.WAVES, highs);
        assertEquals(DamageAmpRing.WAVES, lows);
    }

    @Test
    void wavePeaksAtTheAmplitude() {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        int steps = 3600 * DamageAmpRing.WAVES;
        for (int i = 0; i < steps; i++) {
            double height = DamageAmpRing.height(FULL_TURN * i / steps);
            max = Math.max(max, height);
            min = Math.min(min, height);
        }
        assertEquals(DamageAmpRing.AMPLITUDE, max, 1e-6);
        assertEquals(-DamageAmpRing.AMPLITUDE, min, 1e-6);
    }

    @Test
    void ringHasTheConfiguredRadius() {
        for (int i = 0; i < 16; i++) {
            Vector3 p = DamageAmpRing.point(i * FULL_TURN / 16);
            assertEquals(DamageAmpRing.RADIUS, Math.hypot(p.x(), p.z()), EPS);
        }
    }

    @Test
    void sphereCompletesOneRevolutionInTheConfiguredTicks() {
        int ticks = DamageAmpRing.REVOLUTION_TICKS;
        assertEquals(0, DamageAmpRing.sphereAngle(0), EPS);
        assertEquals(Math.PI, DamageAmpRing.sphereAngle(ticks / 2.0), EPS);
        assertEquals(DamageAmpRing.sphereAngle(3), DamageAmpRing.sphereAngle(3 + ticks), EPS);
        assertTrue(DamageAmpRing.sphereAngle(ticks - 1e-6) > FULL_TURN - 1e-3, "just before the end of a revolution");
    }
}

package omtteam.openmodularturrets.core.geometry;

import omtteam.openmodularturrets.core.world.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the ring against its own parameters, so tuning them does not break the tests.
 */
class WavyRingTest {
    private static final double EPS = 1e-9;
    private static final double FULL_TURN = 2 * Math.PI;
    private static final WavyRing RING = new WavyRing(0.375D, 3, 0.03D, 20);

    @Test
    void ringClosesWithoutASeam() {
        Vector3 start = RING.point(0);
        Vector3 end = RING.point(FULL_TURN);
        assertEquals(start.x(), end.x(), EPS);
        assertEquals(start.y(), end.y(), EPS);
        assertEquals(start.z(), end.z(), EPS);
        // the slope matches too, so the wave continues smoothly over the join
        double slopeStart = RING.height(1e-6) - RING.height(0);
        double slopeEnd = RING.height(FULL_TURN + 1e-6) - RING.height(FULL_TURN);
        assertEquals(slopeStart, slopeEnd, EPS);
    }

    @Test
    void hasAsManyHighsAndLowsAsWaves() {
        int highs = 0;
        int lows = 0;
        int steps = 3600 * RING.waves();
        for (int i = 0; i < steps; i++) {
            double before = RING.height(FULL_TURN * (i - 1) / steps);
            double now = RING.height(FULL_TURN * i / steps);
            double after = RING.height(FULL_TURN * (i + 1) / steps);
            if (now > before && now >= after) highs++;
            if (now < before && now <= after) lows++;
        }
        assertEquals(RING.waves(), highs);
        assertEquals(RING.waves(), lows);
    }

    @Test
    void wavePeaksAtTheAmplitude() {
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        int steps = 3600 * RING.waves();
        for (int i = 0; i < steps; i++) {
            double height = RING.height(FULL_TURN * i / steps);
            max = Math.max(max, height);
            min = Math.min(min, height);
        }
        assertEquals(RING.amplitude(), max, 1e-6);
        assertEquals(-RING.amplitude(), min, 1e-6);
    }

    @Test
    void flatRingWithoutWaves() {
        WavyRing flat = new WavyRing(1, 0, 0.5D, 10);
        for (int i = 0; i < 16; i++) {
            assertEquals(0, flat.height(i * FULL_TURN / 16), EPS);
        }
    }

    @Test
    void ringHasTheConfiguredRadius() {
        for (int i = 0; i < 16; i++) {
            Vector3 p = RING.point(i * FULL_TURN / 16);
            assertEquals(RING.radius(), Math.hypot(p.x(), p.z()), EPS);
        }
    }

    @Test
    void orbitCompletesOneRevolutionInTheConfiguredTicks() {
        double ticks = RING.revolutionTicks();
        assertEquals(0, RING.orbitAngle(0), EPS);
        assertEquals(Math.PI, RING.orbitAngle(ticks / 2.0), EPS);
        assertEquals(RING.orbitAngle(3), RING.orbitAngle(3 + ticks), EPS);
        assertTrue(RING.orbitAngle(ticks - 1e-6) > FULL_TURN - 1e-3, "just before the end of a revolution");
    }

    @Test
    void rejectsInvalidParameters() {
        assertThrows(IllegalArgumentException.class, () -> new WavyRing(0, 3, 0.03D, 20));
        assertThrows(IllegalArgumentException.class, () -> new WavyRing(1, -1, 0.03D, 20));
        assertThrows(IllegalArgumentException.class, () -> new WavyRing(1, 3, 0.03D, 0));
    }
}

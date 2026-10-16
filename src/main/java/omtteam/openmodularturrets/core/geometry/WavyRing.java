package omtteam.openmodularturrets.core.geometry;

import omtteam.openmodularturrets.core.world.Vector3;

/**
 * A horizontal circle whose height follows a sine wave, with a point orbiting on it. The wave repeats a whole number
 * of times around the circle, so the ring closes without a seam.
 * <p>
 * Positions are relative to the ring centre (x/z = ring plane, y = up).
 *
 * @param radius          ring radius in blocks
 * @param waves           number of highs (and of lows) around the ring
 * @param amplitude       height of the wave crests above and the troughs below the ring centre, in blocks
 * @param revolutionTicks ticks the orbiting point needs for one revolution
 */
public record WavyRing(double radius, int waves, double amplitude, double revolutionTicks) {
    public WavyRing {
        if (radius <= 0) throw new IllegalArgumentException("radius must be positive: " + radius);
        if (waves < 0) throw new IllegalArgumentException("waves must not be negative: " + waves);
        if (revolutionTicks <= 0) throw new IllegalArgumentException("revolutionTicks must be positive: " + revolutionTicks);
    }

    /** Height of the ring above its centre plane at {@code angle} (radians); periodic with a period of 2π. */
    public double height(double angle) {
        return amplitude * Math.sin(waves * angle);
    }

    /** Point of the ring at {@code angle} (radians). */
    public Vector3 point(double angle) {
        return new Vector3(radius * Math.cos(angle), height(angle), radius * Math.sin(angle));
    }

    /** Angle (radians) of the orbiting point {@code ticks} ticks into the animation (fractions allowed). */
    public double orbitAngle(double ticks) {
        return 2.0D * Math.PI * (ticks % revolutionTicks) / revolutionTicks;
    }
}

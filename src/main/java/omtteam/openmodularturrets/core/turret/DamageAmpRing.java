package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Vector3;

/**
 * Geometry of the ring a turret shows above its baseplate while its base has a damage amp addon: a circle whose
 * height follows a sine wave. The wave repeats a whole number of times around the circle, so the ring closes without
 * a seam. A glowing sphere orbits on it (client rendering only, see {@code DamageAmpRingRenderer}).
 * <p>
 * Positions are relative to the ring centre in the turret's local frame (x = right, y = up, z = forward).
 */
public final class DamageAmpRing {
    /** Ring diameter of 0.75 blocks. */
    public static final double RADIUS = 0.375D;
    /** Number of highs (and of lows) around the ring. */
    public static final int WAVES = 3;
    /** Height of the wave crests above and the troughs below the ring centre, in blocks. */
    public static final double AMPLITUDE = 0.03D;
    /** Ticks the sphere needs for one revolution (about one second). */
    public static final int REVOLUTION_TICKS = 20;

    private DamageAmpRing() {
    }

    /** Height of the ring above its centre plane at {@code angle} (radians); periodic with a period of 2π. */
    public static double height(double angle) {
        return AMPLITUDE * Math.sin(WAVES * angle);
    }

    /** Point of the ring at {@code angle} (radians). */
    public static Vector3 point(double angle) {
        return new Vector3(RADIUS * Math.cos(angle), height(angle), RADIUS * Math.sin(angle));
    }

    /** Angle (radians) of the sphere {@code ticks} ticks into the animation (fractions allowed). */
    public static double sphereAngle(double ticks) {
        return 2.0D * Math.PI * (ticks % REVOLUTION_TICKS) / REVOLUTION_TICKS;
    }
}

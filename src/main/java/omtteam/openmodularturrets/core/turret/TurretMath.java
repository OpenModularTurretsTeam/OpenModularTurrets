package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;

/**
 * Aiming maths shared by the server (where to shoot) and the renderer (how to rotate the model), so both agree.
 * <p>
 * Every mounting face has a right-handed local frame: {@code up} is the face direction (away from the base),
 * {@code forward} is where a turret with yaw 0 points, {@code right = up × forward}. Yaw turns around {@code up}
 * (0° = forward, 90° = right); pitch is the elevation above the mounting plane (90° = straight away from the base).
 */
public final class TurretMath {
    /** 1.12.2 {@code ProjectileTurret.doTargetedShot}: travel speed used to estimate the flight time. */
    private static final double SPEED_STRAIGHT = 3.0D;
    private static final double SPEED_GRAVITY = 1.6D;
    /** The aim point is lowered by 0.1 blocks. */
    private static final double AIM_DROP = 0.1D;
    /**
     * Lowest local pitch a turret can aim at; anything lower would point into its own base (the base block covers
     * up to about -45° below the mounting plane). New; the 1.12.2 turrets had no effective limits.
     */
    public static final float MIN_PITCH = -45.0F;

    private TurretMath() {
    }

    /** Local axes of a mounting face, in world coordinates. */
    public record Frame(Vector3 right, Vector3 up, Vector3 forward) {
        public Vector3 toLocal(Vector3 world) {
            return new Vector3(world.dot(right), world.dot(up), world.dot(forward));
        }

        public Vector3 toWorld(Vector3 local) {
            return right.scale(local.x()).add(up.scale(local.y())).add(forward.scale(local.z()));
        }
    }

    /** Local yaw and pitch in degrees. */
    public record Angles(float yaw, float pitch) {
        public static final Angles ZERO = new Angles(0, 0);
    }

    /**
     * Turrets on top of a base face +Z (south) at yaw 0, turrets hanging below face -Z, turrets on side faces face
     * world up at yaw 0 (towards the sky).
     */
    public static Frame frame(Face face) {
        Vector3 up = Vector3.of(face);
        Vector3 forward = switch (face) {
            case UP -> new Vector3(0, 0, 1);
            case DOWN -> new Vector3(0, 0, -1);
            default -> new Vector3(0, 1, 0);
        };
        return new Frame(up.cross(forward), up, forward);
    }

    /** Yaw/pitch that point a turret on {@code face} along {@code direction} (world space, any length). */
    public static Angles anglesFor(Face face, Vector3 direction) {
        Vector3 local = frame(face).toLocal(direction);
        double horizontal = Math.sqrt(local.x() * local.x() + local.z() * local.z());
        float yaw = (float) Math.toDegrees(Math.atan2(local.x(), local.z()));
        float pitch = (float) Math.toDegrees(Math.atan2(local.y(), horizontal));
        return new Angles(yaw, pitch);
    }

    /** Whether a turret on {@code face} can turn towards {@code direction} without pointing into its base. */
    public static boolean canAim(Face face, Vector3 direction) {
        return anglesFor(face, direction).pitch() >= MIN_PITCH;
    }

    /** {@code angles} with the pitch limited to {@link #MIN_PITCH}. */
    public static Angles limit(Angles angles) {
        return angles.pitch() >= MIN_PITCH ? angles : new Angles(angles.yaw(), MIN_PITCH);
    }

    /** World direction (unit length) of a turret on {@code face} with the given angles. */
    public static Vector3 direction(Face face, Angles angles) {
        double yaw = Math.toRadians(angles.yaw());
        double pitch = Math.toRadians(angles.pitch());
        Vector3 local = new Vector3(Math.sin(yaw) * Math.cos(pitch), Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        return frame(face).toWorld(local);
    }

    /** Where to shoot and how fast, see {@link #lead}. */
    public record Lead(Vector3 direction, float speedFactor) {
    }

    /**
     * 1.12.2 {@code ProjectileTurret.doTargetedShot}: estimates the flight time from the distance (projectiles with
     * gravity are assumed slower), moves the aim point along the target's velocity for that time, lowers it by 0.1
     * and scales the projectile speed so it arrives in time.
     *
     * @param offset   target aim point minus turret centre
     * @param velocity target movement per tick
     */
    public static Lead lead(Vector3 offset, Vector3 velocity, boolean gravity) {
        double distance = offset.length();
        if (distance < 1.0E-6) return new Lead(offset, 1.0F);
        double time = distance / (gravity ? SPEED_GRAVITY : SPEED_STRAIGHT);
        Vector3 adjusted = offset.add(velocity.scale(time));
        float speedFactor = (float) (adjusted.length() / distance);
        return new Lead(adjusted.subtract(new Vector3(0, AIM_DROP, 0)), speedFactor);
    }

    /**
     * Shortest signed difference {@code to - from} in degrees, in [-180, 180).
     */
    public static float angleDifference(float from, float to) {
        float difference = (to - from) % 360.0F;
        if (difference >= 180.0F) difference -= 360.0F;
        if (difference < -180.0F) difference += 360.0F;
        return difference;
    }
}

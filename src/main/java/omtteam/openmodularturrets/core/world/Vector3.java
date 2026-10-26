package omtteam.openmodularturrets.core.world;

import omtteam.omlib.core.world.Face;

/**
 * Immutable 3D vector (in world coordinates, or a contraption's plot coordinates, see {@link Pose}).
 */
public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    /** The unit vector pointing out of {@code face}. */
    public static Vector3 of(Face face) {
        return new Vector3(face.getStepX(), face.getStepY(), face.getStepZ());
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 scale(double factor) {
        return new Vector3(x * factor, y * factor, z * factor);
    }

    public double dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /** Cross product, right-handed: {@code x × y = z}. */
    public Vector3 cross(Vector3 other) {
        return new Vector3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }
}

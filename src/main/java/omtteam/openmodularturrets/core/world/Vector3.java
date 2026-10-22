package omtteam.openmodularturrets.core.world;

/**
 * Immutable 3D vector in world coordinates (a record in 1.20.1+).
 */
public final class Vector3 {
    public static final Vector3 ZERO = new Vector3(0, 0, 0);

    private final double x;
    private final double y;
    private final double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** The unit vector pointing out of {@code face}. */
    public static Vector3 of(Face face) {
        return new Vector3(face.getStepX(), face.getStepY(), face.getStepZ());
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3)) return false;
        Vector3 other = (Vector3) o;
        return Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0 && Double.compare(z, other.z) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        return 31 * result + Double.hashCode(z);
    }

    @Override
    public String toString() {
        return "Vector3[x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}

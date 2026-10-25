package omtteam.openmodularturrets.core.world;

/**
 * Immutable rotation quaternion {@code (x, y, z, w)}, the same convention as JOML (which Sable poses use). It is
 * expected to have unit length; {@link #rotate} and {@link #inverseRotate} do not normalise it.
 */
public record Quaternion(double x, double y, double z, double w) {

    /**
     * Rotates {@code v} by this quaternion ({@code q v q*}), computed as {@code v + w t + u × t} with
     * {@code u = (x, y, z)} and {@code t = 2 (u × v)}.
     */
    public Vector3 rotate(Vector3 v) {
        return rotate(x, y, z, w, v);
    }

    /** Rotates {@code v} by the inverse rotation (the conjugate {@code (-x, -y, -z, w)} of a unit quaternion). */
    public Vector3 inverseRotate(Vector3 v) {
        return rotate(-x, -y, -z, w, v);
    }

    private static Vector3 rotate(double x, double y, double z, double w, Vector3 v) {
        Vector3 u = new Vector3(x, y, z);
        Vector3 t = u.cross(v).scale(2.0D);
        return v.add(t.scale(w)).add(u.cross(t));
    }
}

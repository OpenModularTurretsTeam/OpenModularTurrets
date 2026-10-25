package omtteam.openmodularturrets.core.world;

/**
 * Where a contraption's plot sits in the world (a Sable "pose"). Blocks of a Sable contraption are stored in a plot
 * far away from where they are shown; a point of the plot ({@code local}) is shown at
 * {@code world = orientation · ((local − rotationPoint) ∘ scale) + position}, where {@code ∘} multiplies per axis.
 * Blocks outside contraptions use {@link #IDENTITY}.
 *
 * @param position      world position of the rotation point
 * @param orientation   rotation of the plot (unit quaternion)
 * @param rotationPoint plot point the contraption rotates and scales around
 * @param scale         scale per axis (non-zero)
 */
public record Pose(Vector3 position, Quaternion orientation, Vector3 rotationPoint, Vector3 scale) {
    /** No contraption: plot and world coordinates are the same. */
    public static final Pose IDENTITY = new Pose(Vector3.ZERO, new Quaternion(0, 0, 0, 1), Vector3.ZERO,
            new Vector3(1, 1, 1));

    /** The world point a plot point is shown at. */
    public Vector3 toWorld(Vector3 local) {
        return orientation.rotate(multiply(local.subtract(rotationPoint), scale)).add(position);
    }

    /** The plot point shown at a world point (inverse of {@link #toWorld}). */
    public Vector3 toLocal(Vector3 world) {
        return divide(orientation.inverseRotate(world.subtract(position)), scale).add(rotationPoint);
    }

    /** Rotation and scale only, for directions and velocities (like Sable's {@code transformNormal}). */
    public Vector3 directionToWorld(Vector3 local) {
        return orientation.rotate(multiply(local, scale));
    }

    /** Inverse of {@link #directionToWorld}. */
    public Vector3 directionToLocal(Vector3 world) {
        return divide(orientation.inverseRotate(world), scale);
    }

    /** Whether this is {@link #IDENTITY}, i.e. the block is not on a contraption. */
    public boolean isIdentity() {
        return equals(IDENTITY);
    }

    private static Vector3 multiply(Vector3 a, Vector3 b) {
        return new Vector3(a.x() * b.x(), a.y() * b.y(), a.z() * b.z());
    }

    private static Vector3 divide(Vector3 a, Vector3 b) {
        return new Vector3(a.x() / b.x(), a.y() / b.y(), a.z() / b.z());
    }
}

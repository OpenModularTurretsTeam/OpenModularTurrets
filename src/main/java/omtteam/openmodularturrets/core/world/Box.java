package omtteam.openmodularturrets.core.world;

import omtteam.omlib.core.world.BlockPosition;

/**
 * Axis-aligned box (in world coordinates, or a contraption's plot coordinates, see {@link Pose}).
 */
public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    /**
     * The cube 1.12.2 used around a block: from {@code pos - radius} to {@code pos + radius} on every axis
     * (block corner coordinates, as in 1.12.2's {@code AxisAlignedBB}).
     */
    public static Box around(BlockPosition pos, double radius) {
        return new Box(pos.x() - radius, pos.y() - radius, pos.z() - radius,
                pos.x() + radius, pos.y() + radius, pos.z() + radius);
    }

    /** Whether the point is inside or on the border of the box. */
    public boolean contains(double x, double y, double z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
}

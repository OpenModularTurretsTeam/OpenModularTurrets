package omtteam.openmodularturrets.core.world;

/**
 * Axis-aligned box in world coordinates.
 */
public record Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    /**
     * The cube oldmod used around a block: from {@code pos - radius} to {@code pos + radius} on every axis
     * (block corner coordinates, as in oldmod's {@code AxisAlignedBB}).
     */
    public static Box around(BlockPosition pos, double radius) {
        return new Box(pos.x() - radius, pos.y() - radius, pos.z() - radius,
                pos.x() + radius, pos.y() + radius, pos.z() + radius);
    }

    public boolean contains(double x, double y, double z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
}

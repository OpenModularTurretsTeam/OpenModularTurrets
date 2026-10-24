package omtteam.openmodularturrets.core.world;

/**
 * Axis-aligned box in world coordinates (a record in 1.20.1+).
 */
public final class Box {
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    /**
     * The cube 1.12.2 used around a block: from {@code pos - radius} to {@code pos + radius} on every axis
     * (block corner coordinates, as in 1.12.2's {@code AxisAlignedBB}).
     */
    public static Box around(BlockPosition pos, double radius) {
        return new Box(pos.x() - radius, pos.y() - radius, pos.z() - radius,
                pos.x() + radius, pos.y() + radius, pos.z() + radius);
    }

    public double minX() {
        return minX;
    }

    public double minY() {
        return minY;
    }

    public double minZ() {
        return minZ;
    }

    public double maxX() {
        return maxX;
    }

    public double maxY() {
        return maxY;
    }

    public double maxZ() {
        return maxZ;
    }

    /** Whether the point is inside or on the border of the box. */
    public boolean contains(double x, double y, double z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        Box other = (Box) o;
        // Double.compare, like Double.hashCode: 0.0 and -0.0 differ
        return Double.compare(minX, other.minX) == 0 && Double.compare(minY, other.minY) == 0
                && Double.compare(minZ, other.minZ) == 0 && Double.compare(maxX, other.maxX) == 0
                && Double.compare(maxY, other.maxY) == 0 && Double.compare(maxZ, other.maxZ) == 0;
    }

    @Override
    public int hashCode() {
        int result = Double.hashCode(minX);
        result = 31 * result + Double.hashCode(minY);
        result = 31 * result + Double.hashCode(minZ);
        result = 31 * result + Double.hashCode(maxX);
        result = 31 * result + Double.hashCode(maxY);
        return 31 * result + Double.hashCode(maxZ);
    }

    @Override
    public String toString() {
        return "Box[" + minX + ", " + minY + ", " + minZ + " -> " + maxX + ", " + maxY + ", " + maxZ + "]";
    }
}

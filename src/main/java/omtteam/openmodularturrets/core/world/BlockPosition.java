package omtteam.openmodularturrets.core.world;

import java.util.Objects;

/**
 * Block coordinates in the world (a record in 1.20.1+).
 */
public final class BlockPosition {
    private final int x;
    private final int y;
    private final int z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    /** The neighbouring position on {@code face}. */
    public BlockPosition relative(Face face) {
        return new BlockPosition(x + face.getStepX(), y + face.getStepY(), z + face.getStepZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockPosition)) return false;
        BlockPosition other = (BlockPosition) o;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public String toString() {
        return "BlockPosition[x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}

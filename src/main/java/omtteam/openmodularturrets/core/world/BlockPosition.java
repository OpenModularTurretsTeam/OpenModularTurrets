package omtteam.openmodularturrets.core.world;

/**
 * Block coordinates in the world.
 */
public record BlockPosition(int x, int y, int z) {
    /** The neighbouring position on {@code face}. */
    public BlockPosition relative(Face face) {
        return new BlockPosition(x + face.getStepX(), y + face.getStepY(), z + face.getStepZ());
    }
}

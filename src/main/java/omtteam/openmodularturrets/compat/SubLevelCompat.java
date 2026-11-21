package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.world.Pose;

/**
 * Sub-levels: groups of blocks that a mod moves through the world. Their blocks are stored in a plot far away from
 * where they are shown, and a {@link Pose} places the plot in the world. No sub-level mod is supported on this branch,
 * so every block is outside a sub-level and every pose is {@link Pose#IDENTITY}.
 * <p>
 * Poses change every tick: ask for them when needed, never keep them.
 */
public final class SubLevelCompat {
    /** Velocities of sub-levels are given in blocks (metres) per second. */
    private static final double TICKS_PER_SECOND = 20.0D;

    private SubLevelCompat() {
    }

    /** Current (logical) pose of the sub-level whose plot contains {@code pos}; {@link Pose#IDENTITY} if none. */
    @SuppressWarnings("unused")
    public static Pose poseAt(Level level, BlockPos pos) {
        return Pose.IDENTITY;
    }

    /**
     * Velocity of the sub-level at a point of its plot, in world coordinates and blocks per tick (it includes the
     * sub-level's rotation); {@link Vec3#ZERO} if the point is not on a sub-level.
     */
    @SuppressWarnings("unused")
    public static Vec3 velocityAt(Level level, Vec3 plotPoint) {
        return Vec3.ZERO;
    }

}

package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.world.Pose;

/**
 * Sable contraptions (sub-levels), through Sable Companion: their blocks are stored in a plot far away from where
 * they are shown, and a {@link Pose} places the plot in the world. Without Sable (and on 1.20.1 and 1.21.11, which have
 * neither Sable nor Companion) every block is outside a contraption.
 * <p>
 * Poses change every tick: ask for them when needed, never keep them.
 */
public final class SubLevelCompat {
    /** Companion gives velocities in blocks (metres) per second. */
    private static final double TICKS_PER_SECOND = 20.0D;

    private SubLevelCompat() {
    }

    /** Current (logical) pose of the contraption whose plot contains {@code pos}; {@link Pose#IDENTITY} if none. */
    @SuppressWarnings("unused")
    public static Pose poseAt(Level level, BlockPos pos) {
        return Pose.IDENTITY;
    }

    /**
     * Velocity of the contraption at a point of its plot, in world coordinates and blocks per tick (it includes the
     * contraption's rotation); {@link Vec3#ZERO} if the point is not on a contraption.
     */
    @SuppressWarnings("unused")
    public static Vec3 velocityAt(Level level, Vec3 plotPoint) {
        return Vec3.ZERO;
    }

}

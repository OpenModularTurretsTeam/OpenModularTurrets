package omtteam.openmodularturrets.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.world.Pose;
//? if >=1.21 && <1.21.11 {
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import omtteam.openmodularturrets.core.world.Quaternion;
import omtteam.openmodularturrets.core.world.Vector3;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
//?}

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
        //? if >=1.21 && <1.21.11 {
        SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(level, pos);
        return subLevel == null ? Pose.IDENTITY : toPose(subLevel.logicalPose());
        //?} else {
        /*return Pose.IDENTITY;
        *///?}
    }

    /**
     * Velocity of the contraption at a point of its plot, in world coordinates and blocks per tick (it includes the
     * contraption's rotation); {@link Vec3#ZERO} if the point is not on a contraption.
     */
    @SuppressWarnings("unused")
    public static Vec3 velocityAt(Level level, Vec3 plotPoint) {
        //? if >=1.21 && <1.21.11 {
        Vector3d velocity = SableCompanion.INSTANCE.getVelocity(level, new Vector3d(plotPoint.x, plotPoint.y, plotPoint.z));
        return new Vec3(velocity.x, velocity.y, velocity.z).scale(1.0D / TICKS_PER_SECOND);
        //?} else {
        /*return Vec3.ZERO;
        *///?}
    }

    //? if >=1.21 && <1.21.11 {
    // Companion's pose as a core Pose (same formula); line comments only, this block is commented out on 1.20.1
    public static Pose toPose(Pose3dc pose) {
        Quaterniondc orientation = pose.orientation();
        return new Pose(toVector(pose.position()),
                new Quaternion(orientation.x(), orientation.y(), orientation.z(), orientation.w()),
                toVector(pose.rotationPoint()), toVector(pose.scale()));
    }

    private static Vector3 toVector(Vector3dc vector) {
        return new Vector3(vector.x(), vector.y(), vector.z());
    }
    //?}
}

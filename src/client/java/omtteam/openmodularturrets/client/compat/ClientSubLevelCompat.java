package omtteam.openmodularturrets.client.compat;

import net.minecraft.core.BlockPos;
import omtteam.openmodularturrets.core.world.Pose;

/**
 * Client side of {@link omtteam.openmodularturrets.compat.SubLevelCompat}: where Sable contraptions are drawn.
 */
public final class ClientSubLevelCompat {
    private ClientSubLevelCompat() {
    }

    /**
     * Pose the contraption whose plot contains {@code pos} is drawn with this frame (interpolated with
     * {@code partialTick}); {@link Pose#IDENTITY} if none.
     */
    @SuppressWarnings("unused")
    public static Pose renderPoseAt(BlockPos pos, float partialTick) {
        return Pose.IDENTITY;
    }
}

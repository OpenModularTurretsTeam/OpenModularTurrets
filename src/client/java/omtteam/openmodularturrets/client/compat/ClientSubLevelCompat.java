package omtteam.openmodularturrets.client.compat;

import net.minecraft.core.BlockPos;
import omtteam.openmodularturrets.core.world.Pose;

/**
 * Client side of {@link omtteam.openmodularturrets.compat.SubLevelCompat}: where sub-levels are drawn. No sub-level mod
 * is supported on this branch, so every block is outside a sub-level.
 */
public final class ClientSubLevelCompat {
    private ClientSubLevelCompat() {
    }

    /**
     * Pose the sub-level whose plot contains {@code pos} is drawn with this frame (interpolated with
     * {@code partialTick}); {@link Pose#IDENTITY} if none.
     */
    @SuppressWarnings("unused")
    public static Pose renderPoseAt(BlockPos pos, float partialTick) {
        return Pose.IDENTITY;
    }
}

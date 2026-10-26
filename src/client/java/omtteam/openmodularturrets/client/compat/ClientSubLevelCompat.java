package omtteam.openmodularturrets.client.compat;

import net.minecraft.core.BlockPos;
import omtteam.openmodularturrets.core.world.Pose;
//? if >=1.21 && <1.21.11 {
import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import dev.ryanhcode.sable.companion.SableCompanion;
import omtteam.openmodularturrets.compat.SubLevelCompat;
//?}

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
        //? if >=1.21 && <1.21.11 {
        ClientSubLevelAccess subLevel = SableCompanion.INSTANCE.getContainingClient(pos);
        return subLevel == null ? Pose.IDENTITY : SubLevelCompat.toPose(subLevel.renderPose(partialTick));
        //?} else {
        /*return Pose.IDENTITY;
        *///?}
    }
}

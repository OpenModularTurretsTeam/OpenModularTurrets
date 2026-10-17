package omtteam.openmodularturrets.security;

import net.minecraft.core.BlockPos;
import omtteam.openmodularturrets.core.security.TrustManager;

/**
 * A block entity with an owner and a trust list: the turret bases and the camouflage trap. The trusted player screen
 * and its packets only talk to this interface.
 */
public interface TrustHost {
    BlockPos getBlockPos();

    /** Owner, trusted players and every permission check of this block. */
    TrustManager getTrustManager();
}

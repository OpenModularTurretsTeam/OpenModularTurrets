package omtteam.openmodularturrets.security;

import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.TrustManager;
import org.jetbrains.annotations.Nullable;

/**
 * A block entity with an owner and a trust list: the turret bases and the camouflage trap. The trusted player screen
 * and its packets only talk to this interface. The {@link OwnedHost} methods forward to the {@link TrustManager}.
 */
public interface TrustHost extends OwnedHost {
    /** Owner, trusted players and every permission check of this block. */
    TrustManager getTrustManager();

    @Nullable
    @Override
    default PlayerIdentity getOwner() {
        return getTrustManager().getOwner();
    }

    @Override
    default boolean claim(PlayerIdentity player) {
        return getTrustManager().claim(player);
    }

    @Override
    default void setOwner(PlayerIdentity owner) {
        getTrustManager().setOwner(owner);
    }

    @Override
    default void clearOwner() {
        getTrustManager().clearOwner();
    }
}

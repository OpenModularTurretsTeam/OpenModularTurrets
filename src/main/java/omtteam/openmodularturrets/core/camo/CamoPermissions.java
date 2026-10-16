package omtteam.openmodularturrets.core.camo;

import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;
import omtteam.openmodularturrets.core.security.TrustManager;

/**
 * Who may change the camouflage (1.12.2: OMLib {@code handleCamoActivation} was owner-only, the light messages
 * required an admin).
 */
public final class CamoPermissions {
    private CamoPermissions() {
    }

    /** Setting or resetting the disguise of a block with a trust list: the owner only. */
    public static boolean canSetDisguise(TrustManager trust, PlayerInfo player) {
        return trust.canPerformOwnerAction(player);
    }

    /** Setting or resetting the disguise of a block that only has an owner. */
    public static boolean canSetDisguise(PlayerIdentity owner, PlayerInfo player) {
        return owner != null && owner.is(player.uuid());
    }

    /** Changing light value and opacity of a block with a trust list: admins (the owner always is one). */
    public static boolean canChangeLight(TrustManager trust, PlayerInfo player) {
        return trust.isAdmin(player);
    }

    /** Changing light value and opacity of a block that only has an owner: the owner. */
    public static boolean canChangeLight(PlayerIdentity owner, PlayerInfo player) {
        return canSetDisguise(owner, player);
    }
}

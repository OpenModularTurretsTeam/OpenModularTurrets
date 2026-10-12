package omtteam.openmodularturrets.security;

import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;

/**
 * Converts Minecraft players into the core permission types.
 */
public final class PlayerAdapter {
    /** OMLib {@code PlayerUtil.isPlayerOP}: {@code canUseCommand(4, "")}. */
    private static final int OPERATOR_LEVEL = 4;

    private PlayerAdapter() {
    }

    public static PlayerIdentity identity(Player player) {
        return new PlayerIdentity(player.getUUID(), player.getGameProfile().getName());
    }

    public static PlayerInfo info(Player player) {
        return new PlayerInfo(player.getUUID(), player.getGameProfile().getName(), player.hasPermissions(OPERATOR_LEVEL));
    }
}

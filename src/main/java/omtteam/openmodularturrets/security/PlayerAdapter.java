package omtteam.openmodularturrets.security;

import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import omtteam.openmodularturrets.core.security.PlayerIdentity;
import omtteam.openmodularturrets.core.security.PlayerInfo;

/**
 * Converts Minecraft players into the core permission types.
 */
public final class PlayerAdapter {
    private PlayerAdapter() {
    }

    public static PlayerIdentity identity(Player player) {
        return new PlayerIdentity(player.getUUID(), player.nameAndId().name());
    }

    public static PlayerInfo info(Player player) {
        return new PlayerInfo(player.getUUID(), player.nameAndId().name(),
                // OMLib PlayerUtil.isPlayerOP: canUseCommand(4, "")
                player.permissions().hasPermission(Permissions.COMMANDS_OWNER));
    }
}

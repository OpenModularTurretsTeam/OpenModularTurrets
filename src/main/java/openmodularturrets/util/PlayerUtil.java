package openmodularturrets.util;

/**
 * Created by nico on 6/4/15.
 * A lib for all player based functions.
 */

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.TurretBase;

import java.util.Map;
import java.util.UUID;

public class PlayerUtil {
    public static UUID getPlayerUUID(String username) {
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(username)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static UUID getPlayerUIDUnstable(String possibleUUID) {
        if (possibleUUID == null || possibleUUID.isEmpty()) {
            return null;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(possibleUUID);
        } catch (IllegalArgumentException e) {
            uuid = getPlayerUUID(possibleUUID);
        }
        return uuid;
    }

    public static String getPlayerNameFromUUID(String possibleUUID) {
        if (possibleUUID == null || possibleUUID.isEmpty()) {
            return null;
        }
        return UsernameCache.getLastKnownUsername(UUID.fromString(possibleUUID));
    }

    public static TrustedPlayer getTrustedPlayer(EntityPlayer player, TurretBase base) {
        if (base.getTrustedPlayer(player.getUniqueID()) != null || (ConfigHandler.offlineModeSupport && base.getTrustedPlayer(player.getName()) != null)) {
            return (base.getTrustedPlayer(player.getUniqueID()) == null ? base.getTrustedPlayer(player.getName()) : base.getTrustedPlayer(player.getUniqueID()));
        } else {
            return null;
        }
    }

    public static boolean isPlayerOwner(EntityPlayer player, TurretBase base) {
        return (base.getOwner().equals(player.getUniqueID().toString()) ||
                (ConfigHandler.offlineModeSupport && base.getOwnerName().equals(player.getName())));
    }
}

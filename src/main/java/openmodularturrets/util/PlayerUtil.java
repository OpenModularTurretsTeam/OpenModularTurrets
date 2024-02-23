package openmodularturrets.util;

/**
 * Created by nico on 6/4/15.
 */

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.UsernameCache;

import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBase;

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
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getKey().toString().equals(possibleUUID)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static TrustedPlayer getTrustedPlayer(EntityPlayer player, TurretBase base) {
        if (base.getTrustedPlayer(player.getUniqueID()) != null
                || (ConfigHandler.offlineModeSupport && base.getTrustedPlayer(player.getDisplayName()) != null)) {
            return (base.getTrustedPlayer(player.getUniqueID()) == null ? base.getTrustedPlayer(player.getDisplayName())
                    : base.getTrustedPlayer(player.getUniqueID()));
        } else {
            return null;
        }
    }

    public static void setBaseOwner(EntityPlayer player, TurretBase base) {
        if (!ConfigHandler.offlineModeSupport) {
            base.setOwner(player.getUniqueID().toString());
        }
    }

    public static boolean isPlayerOwner(EntityPlayer player, TurretBase base) {
        return (base.getOwner().equals(player.getUniqueID().toString())
                || (ConfigHandler.offlineModeSupport && base.getOwnerName().equals(player.getDisplayName())));
    }

    public static boolean isPlayerNameValid(String name) {
        for (Map.Entry<UUID, String> entry : UsernameCache.getMap().entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerAdmin(EntityPlayer player, TurretBase machine) {
        return isPlayerOwner(player, machine)
                || (getTrustedPlayer(player, machine) != null && (getTrustedPlayer(player, machine).admin));
    }
}

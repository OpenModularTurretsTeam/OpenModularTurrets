package omtteam.openmodularturrets.camo;

import net.minecraft.server.level.ServerPlayer;
import omtteam.openmodularturrets.compat.MenuCompat;
import omtteam.openmodularturrets.menu.CamoMenu;

/**
 * Opens the camouflage menu of a {@link CamoHost}.
 */
public final class CamoMenus {
    private CamoMenus() {
    }

    /** The caller checked that the player may use the menu; the menu itself checks every change again. */
    public static void open(ServerPlayer player, CamoHost host) {
        if (!host.isCamoEnabled() || !(host.canSetCamo(player) || host.canChangeLight(player))) return;
        MenuCompat.open(player, player.level().getBlockState(host.getBlockPos()).getBlock().getName(),
                host.getBlockPos(), (containerId, inventory, menuPlayer) -> CamoMenu.server(containerId, inventory, host));
    }
}

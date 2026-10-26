package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import omtteam.openmodularturrets.blockentity.defense.CamoTrapBlockEntity;
import omtteam.omlib.camo.CamoHost;
import omtteam.omlib.client.compat.ClientNetworkCompat;
import omtteam.omlib.menu.CamoMenu;
import omtteam.omlib.network.CamoPackets;
import omtteam.omlib.network.OMLibPackets;
import omtteam.openmodularturrets.security.PlayerAdapter;

import java.util.function.Consumer;

/**
 * The camouflage trap has no other screen: its admins manage the trusted players from its camouflage screen, and its
 * owner can disown it there.
 */
public final class TrapCamoExtras {
    private TrapCamoExtras() {
    }

    public static void addWidgets(CamoHost host, BlockPos pos, int left, int top, int width,
                                  Consumer<AbstractWidget> adder) {
        if (!(host instanceof CamoTrapBlockEntity trap)) return;
        Minecraft minecraft = Minecraft.getInstance();
        Button trusted = Button.builder(ScreenTexts.gui("trusted"), b -> openTrustedPlayers(pos))
                .bounds(left + width - 8 - 64, top + CamoMenu.PHANTOM_Y - 1, 64, 20)
                .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets.trusted_player_gui")))
                .build();
        adder.accept(trusted);
        trusted.active = minecraft.player != null && trap.getTrustManager().isAdmin(PlayerAdapter.info(minecraft.player));
        if (minecraft.player != null && trap.getTrustManager().isOwner(PlayerAdapter.info(minecraft.player))) {
            // outside the panel, whose row is full; "No" asks the server for the camouflage menu again
            adder.accept(DisownConfirm.button(left + width + 4, top + CamoMenu.PHANTOM_Y - 1, 80, pos,
                    () -> ClientNetworkCompat.sendToServer(OMLibPackets.OPEN_CAMO_MENU, new CamoPackets.OpenMenu(pos))));
        }
    }

    /** Replaces the camouflage screen; "Back" asks the server for the camouflage menu again. */
    private static void openTrustedPlayers(BlockPos pos) {
        Minecraft.getInstance().setScreen(new TrustedPlayersScreen(pos,
                () -> ClientNetworkCompat.sendToServer(OMLibPackets.OPEN_CAMO_MENU, new CamoPackets.OpenMenu(pos))));
    }
}

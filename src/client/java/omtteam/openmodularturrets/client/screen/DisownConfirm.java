package omtteam.openmodularturrets.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import omtteam.omlib.client.compat.ClientNetworkCompat;
import omtteam.openmodularturrets.network.OMTPackets;
import omtteam.openmodularturrets.network.TrustPackets;

/**
 * The owner's "Disown" button of the configure screen and the camouflage trap, with its confirmation. The server
 * checks ownership again; open menus of the block close themselves once nobody owns it.
 */
final class DisownConfirm {
    private DisownConfirm() {
    }

    /** A red "Disown" button that asks before disowning the block at {@code pos}; "No" runs {@code onCancel}. */
    static Button button(int x, int y, int width, BlockPos pos, Runnable onCancel) {
        return Button.builder(ScreenTexts.gui("disown").withStyle(ChatFormatting.RED), b -> ask(pos, onCancel))
                .bounds(x, y, width, 20)
                .tooltip(Tooltip.create(Component.translatable("tooltip.openmodularturrets.disown")))
                .build();
    }

    private static void ask(BlockPos pos, Runnable onCancel) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.setScreen(new ConfirmScreen(confirmed -> {
            if (confirmed) {
                ClientNetworkCompat.sendToServer(OMTPackets.DISOWN, new TrustPackets.Disown(pos));
                minecraft.setScreen(null);
            } else {
                onCancel.run();
            }
        }, Component.translatable("gui.openmodularturrets.disown.title"),
                Component.translatable("gui.openmodularturrets.disown.warning")));
    }
}

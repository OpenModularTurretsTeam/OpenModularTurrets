package omtteam.openmodularturrets.client.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.item.ModuleToggles;

/**
 * Marks upgrades and addons that are disabled in the config ({@link ModuleToggles}) in the machine GUIs: a red frame
 * and tint behind their slot, as on 1.20.1+. The "no" symbol over their icon comes from their item model
 * ({@code client.model.DisabledModuleModel}).
 */
@SideOnly(Side.CLIENT)
public final class DisabledModuleOverlay {
    private static final int FRAME_COLOR = 0xFFE01010;
    private static final int TINT_COLOR = 0x50FF0000;

    private DisabledModuleOverlay() {
    }

    /**
     * A red frame and tint behind every machine slot (not the player's inventory) that holds a disabled upgrade or
     * addon; call it at the end of {@code drawGuiContainerBackgroundLayer}, so the item is drawn over it.
     */
    public static void renderSlotFrames(Container container, int left, int top) {
        for (Slot slot : container.inventorySlots) {
            if (slot.inventory instanceof InventoryPlayer || !slot.isEnabled() || !ModuleToggles.isDisabled(slot.getStack())) {
                continue;
            }
            int x = left + slot.xPos;
            int y = top + slot.yPos;
            Gui.drawRect(x, y, x + 16, y + 16, TINT_COLOR);
            Gui.drawRect(x - 1, y - 1, x + 17, y, FRAME_COLOR);
            Gui.drawRect(x - 1, y + 16, x + 17, y + 17, FRAME_COLOR);
            Gui.drawRect(x - 1, y, x, y + 16, FRAME_COLOR);
            Gui.drawRect(x + 16, y, x + 17, y + 16, FRAME_COLOR);
        }
        // drawRect leaves its colour set
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}

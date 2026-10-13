package omtteam.openmodularturrets.client.compat;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Screen base class for the plain (non-container) screens.
 * Subclasses draw their panel in {@link #renderPanel} (behind widgets) and extras in {@link #renderForeground}
 * (above widgets), and handle the mouse wheel in {@link #onScroll}.
 */
public abstract class CompatScreen extends Screen {
    protected CompatScreen(Component title) {
        super(title);
    }

    /** Draws the panel texture and labels; called after the dimmed background, before widgets. */
    protected abstract void renderPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick);

    /** Draws tooltips and other overlays; called after widgets. */
    protected void renderForeground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
    }

    /** Mouse wheel; {@code delta} is positive when scrolling up. */
    protected boolean onScroll(double mouseX, double mouseY, double delta) {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        renderPanel(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        renderForeground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return onScroll(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

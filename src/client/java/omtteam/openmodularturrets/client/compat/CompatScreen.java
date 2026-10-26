package omtteam.openmodularturrets.client.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Screen base class hiding the rendering and scrolling differences between 1.20.1 and 1.21.
 * Subclasses draw their panel in {@link #renderPanel} (behind widgets) and extras in {@link #renderForeground}
 * (above widgets), and handle the mouse wheel in {@link #onScroll}.
 */
public abstract class CompatScreen extends Screen {
    protected CompatScreen(Component title) {
        super(title);
    }

    /** Draws the panel texture and labels; called after the dimmed background, before widgets. */
    protected abstract void renderPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /** Draws tooltips and other overlays; called after widgets. */
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    /** Mouse wheel; {@code delta} is positive when scrolling up. */
    protected boolean onScroll(double mouseX, double mouseY, double delta) {
        return false;
    }

    //? if >=1.21 {
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        //? if >=1.21.11 {
        /*super.renderBackground(graphics, mouseX, mouseY, partialTick);
        *///?} else {
        renderTransparentBackground(graphics);
        //?}
        renderPanel(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderForeground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return onScroll(mouseX, mouseY, scrollY) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        renderPanel(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderForeground(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return onScroll(mouseX, mouseY, delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }
    *///?}

    //? if >=1.21.11 {
    /*// Dims the world like a container screen, without the blur.
    @Override
    public boolean isInGameUi() {
        return true;
    }
    *///?}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

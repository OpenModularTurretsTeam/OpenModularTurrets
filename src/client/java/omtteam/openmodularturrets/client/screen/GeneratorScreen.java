package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.menu.GeneratorMenu;

/**
 * Generator GUI: fuel slot with a furnace-style flame and the turret base energy bar.
 */
public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final Identifier TEXTURE = OMT.id("textures/gui/generator.png");
    private static final int FLAME_SIZE = 14;
    private static final int ENERGY_HEIGHT = 51;

    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        if (x > 153 && x < 167 && y > 17 && y < 68) {
            graphics.setTooltipForNextFrame(font, Component.literal(menu.getEnergy() + "/" + menu.getCapacity() + " RF"), mouseX, mouseY);
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
        int flame = (int) Math.ceil(menu.getBurnProgress() * FLAME_SIZE);
        if (flame > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 81, topPos + 36 + FLAME_SIZE - flame, 176, 72 + FLAME_SIZE - flame,
                    FLAME_SIZE, flame, 256, 256);
        }
        long capacity = menu.getCapacity();
        int filled = (int) (menu.getEnergy() * ENERGY_HEIGHT / (capacity == 0 ? 1 : capacity));
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 153, topPos + 17 + ENERGY_HEIGHT - filled, 196, 68 - filled, 14, filled, 256, 256);
    }
}

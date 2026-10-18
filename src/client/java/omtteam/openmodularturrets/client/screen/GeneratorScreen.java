package omtteam.openmodularturrets.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.menu.GeneratorMenu;

/**
 * Generator GUI: 3×3 fuel slots, the blue upgrade slot, a furnace-style flame and the turret base energy bar. The
 * area around the upgrade slot is left empty for later slots.
 */
public class GeneratorScreen extends AbstractContainerScreen<GeneratorMenu> {
    private static final ResourceLocation TEXTURE = OMT.id("textures/gui/generator.png");
    private static final int FLAME_SIZE = 14;
    /** The flame sits left of the energy bar, level with the middle fuel row. */
    private static final int FLAME_X = 136;
    private static final int FLAME_Y = 36;
    private static final int ENERGY_HEIGHT = 51;

    public GeneratorScreen(GeneratorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ClientCompat.renderContainerBackground(this, graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
        int x = mouseX - leftPos;
        int y = mouseY - topPos;
        if (x > 153 && x < 167 && y > 17 && y < 68) {
            graphics.renderTooltip(font, Component.literal(menu.getEnergy() + "/" + menu.getCapacity() + " RF"), mouseX, mouseY);
        } else if (hoveredSlot != null && !hoveredSlot.hasItem()
                && hoveredSlot.x == GeneratorMenu.UPGRADE_X && hoveredSlot.y == GeneratorMenu.UPGRADE_Y) {
            graphics.renderTooltip(font, Component.translatable("tooltip.openmodularturrets.generator_upgrade_slot"),
                    mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int flame = (int) Math.ceil(menu.getBurnProgress() * FLAME_SIZE);
        if (flame > 0) {
            graphics.blit(TEXTURE, leftPos + FLAME_X, topPos + FLAME_Y + FLAME_SIZE - flame, 176, 72 + FLAME_SIZE - flame,
                    FLAME_SIZE, flame);
        }
        long capacity = menu.getCapacity();
        int filled = (int) (menu.getEnergy() * ENERGY_HEIGHT / (capacity == 0 ? 1 : capacity));
        graphics.blit(TEXTURE, leftPos + 153, topPos + 17 + ENERGY_HEIGHT - filled, 196, 68 - filled, 14, filled);
    }
}

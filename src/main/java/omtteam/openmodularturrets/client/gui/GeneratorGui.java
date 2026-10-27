package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import omtteam.omlib.client.gui.LargeStackGui;
import omtteam.openmodularturrets.container.GeneratorContainer;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Generator GUI (1.20.1+ {@code GeneratorScreen}): 3×3 fuel slots, the upgrade slot, a furnace-style flame and the
 * energy bar; the fuel slots may hold more than a stack (inventory expanders, {@link LargeStackGui}).
 */
public class GeneratorGui extends LargeStackGui {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Reference.MOD_ID, "textures/gui/generator.png");
    private static final int FLAME_SIZE = 14;
    /** The flame sits left of the energy bar, level with the middle fuel row. */
    private static final int FLAME_X = 136;
    private static final int FLAME_Y = 36;
    private static final int ENERGY_HEIGHT = 51;

    private final GeneratorContainer container;

    public GeneratorGui(InventoryPlayer inventoryPlayer, GeneratorTileEntity generator) {
        this(new GeneratorContainer(inventoryPlayer, generator));
    }

    private GeneratorGui(GeneratorContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
        int x = mouseX - guiLeft;
        int y = mouseY - guiTop;
        Slot hovered = getSlotUnderMouse();
        if (x > 153 && x < 167 && y > 17 && y < 68) {
            drawHoveringText(container.getEnergy() + "/" + container.getCapacity() + " RF", mouseX, mouseY);
        } else if (hovered != null && !hovered.getHasStack() && hovered.xPos == GeneratorContainer.UPGRADE_X
                && hovered.yPos == GeneratorContainer.UPGRADE_Y) {
            drawHoveringText(safeLocalize("tooltip.openmodularturrets.generator_upgrade_slot"), mouseX, mouseY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(safeLocalize("tile.generator.name"), 8, 6, 0x000000);
        fontRenderer.drawString(safeLocalize("container.inventory"), 8, ySize - 94, 0x000000);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int flame = (int) Math.ceil(container.getBurnProgress() * FLAME_SIZE);
        if (flame > 0) {
            drawTexturedModalRect(guiLeft + FLAME_X, guiTop + FLAME_Y + FLAME_SIZE - flame, 176,
                    72 + FLAME_SIZE - flame, FLAME_SIZE, flame);
        }
        long capacity = container.getCapacity();
        int filled = (int) (container.getEnergy() * ENERGY_HEIGHT / (capacity == 0 ? 1 : capacity));
        drawTexturedModalRect(guiLeft + 153, guiTop + 17 + ENERGY_HEIGHT - filled, 196, 68 - filled, 14, filled);
    }
}

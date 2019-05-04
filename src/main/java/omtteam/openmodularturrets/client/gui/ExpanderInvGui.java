package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.gui.containers.ExpanderInvContainer;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.Expander;
import org.lwjgl.opengl.GL11;

public class ExpanderInvGui extends GuiContainer {
    public ExpanderInvGui(InventoryPlayer inventoryPlayer, Expander tileEntity) {
        super(new ExpanderInvContainer(inventoryPlayer, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.drawDefaultBackground();
        ResourceLocation texture = (new ResourceLocation(OMTNames.Textures.expanderGUI));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
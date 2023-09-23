package omtteam.openmodularturrets.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import omtteam.openmodularturrets.client.gui.containers.PowerGeneratorContainer;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.tileentity.PowerGenerator;
import org.lwjgl.opengl.GL11;

public class PowerGeneratorGui extends GuiContainer {
    PowerGenerator powerGenerator;

    public PowerGeneratorGui(InventoryPlayer inventoryPlayer, PowerGenerator tileEntity) {
        super(new PowerGeneratorContainer(inventoryPlayer, tileEntity));
        this.powerGenerator = tileEntity;

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        this.drawDefaultBackground();
        ResourceLocation texture = new ResourceLocation(OMTNames.Textures.powerGeneratorGUI + "_off");
        switch (powerGenerator.getState()) {
            case on:
                texture = new ResourceLocation(OMTNames.Textures.powerGeneratorGUI + "_on");
                break;
            case boosted:
                texture = new ResourceLocation(OMTNames.Textures.powerGeneratorGUI + "_on_boosted");
                break;
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
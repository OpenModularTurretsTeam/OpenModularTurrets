package openmodularturrets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.client.gui.containers.ExpanderInvContainer;
import openmodularturrets.client.gui.containers.TurretBaseTierFiveContainer;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.tileentity.expander.AbstractInvExpander;
import openmodularturrets.tileentity.turretbase.TrustedPlayer;
import openmodularturrets.tileentity.turretbase.TurretBaseTierFiveTileEntity;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ExpanderInvGui extends GuiContainer {

    public ExpanderInvGui(InventoryPlayer inventoryPlayer, AbstractInvExpander tileEntity) {
        super(new ExpanderInvContainer(inventoryPlayer, tileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        ResourceLocation texture = (new ResourceLocation(ModInfo.ID + ":textures/gui/extenderInv.png"));
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }


}
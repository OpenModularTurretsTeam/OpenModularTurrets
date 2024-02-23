package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelLever;
import openmodularturrets.client.render.renderers.blockitem.LeverRenderer;
import openmodularturrets.tileentity.LeverTileEntity;

class LeverItemRenderer implements IItemRenderer {

    private final LeverRenderer leverRenderer;
    private final LeverTileEntity leverTileEntity;
    private final ModelLever model;

    public LeverItemRenderer(LeverRenderer leverRenderer, LeverTileEntity leverTileEntity) {
        this.leverRenderer = leverRenderer;
        this.leverTileEntity = leverTileEntity;

        this.model = new ModelLever();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glTranslated(-0.5, -0.5, -0.5);
        this.leverRenderer.renderTileEntityAt(this.leverTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
        GL11.glPopMatrix();
    }
}

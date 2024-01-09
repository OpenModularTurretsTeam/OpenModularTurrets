package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderInvTierTwoRenderer;
import openmodularturrets.tileentity.expander.ExpanderInvTierTwoTileEntity;

class ExpanderInvTierTwoItemRenderer implements IItemRenderer {

    private final ExpanderInvTierTwoRenderer expanderInvTierTwoRenderer;
    private final ExpanderInvTierTwoTileEntity expanderInvTierTwoTileEntity;
    private final ModelExpander model;

    public ExpanderInvTierTwoItemRenderer(ExpanderInvTierTwoRenderer expanderInvTierTwoRenderer,
            ExpanderInvTierTwoTileEntity expanderInvTierTwoTileEntity) {
        this.expanderInvTierTwoRenderer = expanderInvTierTwoRenderer;
        this.expanderInvTierTwoTileEntity = expanderInvTierTwoTileEntity;
        this.model = new ModelExpander();
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
        this.expanderInvTierTwoRenderer.renderTileEntityAt(this.expanderInvTierTwoTileEntity, 0.1D, 0.1D, -0.2D, 0.0F);
        GL11.glPopMatrix();
    }
}

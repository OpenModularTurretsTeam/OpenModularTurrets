package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderPowerTierTwoRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierTwoTileEntity;

class ExpanderPowerTierTwoItemRenderer implements IItemRenderer {

    private final ExpanderPowerTierTwoRenderer expanderPowerTierTwoRenderer;
    private final ExpanderPowerTierTwoTileEntity expanderPowerTierTwoTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierTwoItemRenderer(ExpanderPowerTierTwoRenderer expanderPowerTierTwoRenderer,
            ExpanderPowerTierTwoTileEntity expanderPowerTierTwoTileEntity) {
        this.expanderPowerTierTwoRenderer = expanderPowerTierTwoRenderer;
        this.expanderPowerTierTwoTileEntity = expanderPowerTierTwoTileEntity;
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
        this.expanderPowerTierTwoRenderer
                .renderTileEntityAt(this.expanderPowerTierTwoTileEntity, 0.1D, 0.1D, -0.2D, 0.0F);
        GL11.glPopMatrix();
    }
}

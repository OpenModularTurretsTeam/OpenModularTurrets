package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderTierThreeRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierThreeTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderPowerTierThreeItemRenderer implements IItemRenderer {

    private final ExpanderTierThreeRenderer expanderTierThreeRenderer;
    private final ExpanderPowerTierThreeTileEntity expanderPowerTierThreeTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierThreeItemRenderer(ExpanderTierThreeRenderer expanderTierThreeRenderer, ExpanderPowerTierThreeTileEntity expanderPowerTierThreeTileEntity) {
        this.expanderTierThreeRenderer = expanderTierThreeRenderer;
        this.expanderPowerTierThreeTileEntity = expanderPowerTierThreeTileEntity;
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
        this.expanderTierThreeRenderer.renderTileEntityAt(this.expanderPowerTierThreeTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

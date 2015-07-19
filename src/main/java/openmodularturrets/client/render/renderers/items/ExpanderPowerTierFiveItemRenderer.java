package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderTierFiveRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierFiveTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderPowerTierFiveItemRenderer implements IItemRenderer {

    private final ExpanderTierFiveRenderer expanderTierFiveRenderer;
    private final ExpanderPowerTierFiveTileEntity expanderPowerTierFiveTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierFiveItemRenderer(ExpanderTierFiveRenderer expanderTierFiveRenderer, ExpanderPowerTierFiveTileEntity expanderPowerTierFiveTileEntity) {
        this.expanderTierFiveRenderer = expanderTierFiveRenderer;
        this.expanderPowerTierFiveTileEntity = expanderPowerTierFiveTileEntity;
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
        this.expanderTierFiveRenderer.renderTileEntityAt(this.expanderPowerTierFiveTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

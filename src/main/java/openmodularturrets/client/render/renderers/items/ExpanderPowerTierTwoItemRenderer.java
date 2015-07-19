package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderTierTwoRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierTwoTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderPowerTierTwoItemRenderer implements IItemRenderer {

    private final ExpanderTierTwoRenderer expanderTierTwoRenderer;
    private final ExpanderPowerTierTwoTileEntity expanderPowerTierTwoTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierTwoItemRenderer(ExpanderTierTwoRenderer expanderTierTwoRenderer, ExpanderPowerTierTwoTileEntity expanderPowerTierTwoTileEntity) {
        this.expanderTierTwoRenderer = expanderTierTwoRenderer;
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
        this.expanderTierTwoRenderer.renderTileEntityAt(this.expanderPowerTierTwoTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

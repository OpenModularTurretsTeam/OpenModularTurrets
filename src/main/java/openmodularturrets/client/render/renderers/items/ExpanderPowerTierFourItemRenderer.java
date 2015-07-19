package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderTierFourRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierFourTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderPowerTierFourItemRenderer implements IItemRenderer {

    private final ExpanderTierFourRenderer expanderTierFourRenderer;
    private final ExpanderPowerTierFourTileEntity expanderPowerTierFourTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierFourItemRenderer(ExpanderTierFourRenderer expanderTierFourRenderer, ExpanderPowerTierFourTileEntity expanderPowerTierFourTileEntity) {
        this.expanderTierFourRenderer = expanderTierFourRenderer;
        this.expanderPowerTierFourTileEntity = expanderPowerTierFourTileEntity;
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
        this.expanderTierFourRenderer.renderTileEntityAt(this.expanderPowerTierFourTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

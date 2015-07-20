package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.ExpanderInvTierOneRenderer;
import openmodularturrets.client.render.renderers.blockitem.ExpanderPowerTierOneRenderer;
import openmodularturrets.tileentity.expander.ExpanderInvTierOneTileEntity;
import openmodularturrets.tileentity.expander.ExpanderPowerTierOneTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderInvTierOneItemRenderer implements IItemRenderer {

    private final ExpanderInvTierOneRenderer expanderInvTierOneRenderer;
    private final ExpanderInvTierOneTileEntity expanderInvTierOneTileEntity;
    private final ModelExpander model;

    public ExpanderInvTierOneItemRenderer(ExpanderInvTierOneRenderer expanderInvTierOneRenderer, ExpanderInvTierOneTileEntity expanderInvTierOneTileEntity) {
        this.expanderInvTierOneRenderer = expanderInvTierOneRenderer;
        this.expanderInvTierOneTileEntity = expanderInvTierOneTileEntity;
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
        this.expanderInvTierOneRenderer.renderTileEntityAt(this.expanderInvTierOneTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelDisposableItemTurret;
import openmodularturrets.client.render.models.ModelExpander;
import openmodularturrets.client.render.renderers.blockitem.DisposableItemTurretRenderer;
import openmodularturrets.client.render.renderers.blockitem.ExpanderTierOneRenderer;
import openmodularturrets.tileentity.expander.ExpanderPowerTierOneTileEntity;
import openmodularturrets.tileentity.turrets.DisposableItemTurretTileEntity;
import org.lwjgl.opengl.GL11;

public class ExpanderPowerTierOneItemRenderer implements IItemRenderer {

    private final ExpanderTierOneRenderer expanderTierOneRenderer;
    private final ExpanderPowerTierOneTileEntity expanderPowerTierOneTileEntity;
    private final ModelExpander model;

    public ExpanderPowerTierOneItemRenderer(ExpanderTierOneRenderer expanderTierOneRenderer, ExpanderPowerTierOneTileEntity expanderPowerTierOneTileEntity) {
        this.expanderTierOneRenderer = expanderTierOneRenderer;
        this.expanderPowerTierOneTileEntity = expanderPowerTierOneTileEntity;
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
        this.expanderTierOneRenderer.renderTileEntityAt(this.expanderPowerTierOneTileEntity, 0.0D, 0.0D, 0.0D,
                0.0F);
        GL11.glPopMatrix();
    }
}

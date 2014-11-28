package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelLever;
import openmodularturrets.client.render.renderers.blockitem.LeverRenderer;
import openmodularturrets.tileentity.LeverTileEntity;

public class LeverItemRenderer implements IItemRenderer {

    private final LeverRenderer leverRenderer;
    private final LeverTileEntity leverTileEntity;
    private ModelLever model;

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
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        this.leverRenderer.renderTileEntityAt(this.leverTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelLever;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class LeverItemRenderer implements IItemRenderer {

	private ModelLever model;

	public LeverItemRenderer() {

		model = new ModelLever();
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
	    //TileEntityRenderer.instance.renderTileEntityAt(new LeverTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

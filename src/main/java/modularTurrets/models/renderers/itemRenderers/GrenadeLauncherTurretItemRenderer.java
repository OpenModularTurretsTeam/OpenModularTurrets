package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelGrenadeLauncher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class GrenadeLauncherTurretItemRenderer implements IItemRenderer {

	private ModelGrenadeLauncher model;

	public GrenadeLauncherTurretItemRenderer() {

		model = new ModelGrenadeLauncher();
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
		//TileEntityRenderer.instance.renderTileEntityAt(new GrenadeLauncherTurretTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

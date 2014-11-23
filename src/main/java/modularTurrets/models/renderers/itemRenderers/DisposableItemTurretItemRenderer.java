package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelDisposableItemTurret;
import modularTurrets.tileEntities.turrets.DisposableItemTurretTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class DisposableItemTurretItemRenderer implements IItemRenderer {

	private ModelDisposableItemTurret model;

	public DisposableItemTurretItemRenderer() {

		model = new ModelDisposableItemTurret();
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
	TileEntityRenderer.instance.renderTileEntityAt(new DisposableItemTurretTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelLaserTurret;
import modularTurrets.tileEntities.turrets.LaserTurretTileEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class LaserTurretItemRenderer implements IItemRenderer {

	private ModelLaserTurret model;

	public LaserTurretItemRenderer() {

		model = new ModelLaserTurret();
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
	TileEntityRenderer.instance.renderTileEntityAt(new LaserTurretTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

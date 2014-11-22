package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelRocketTurret;
import modularTurrets.tileentity.turrets.RocketTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RocketTurretItemRenderer implements IItemRenderer {

	private ModelRocketTurret model;

	public RocketTurretItemRenderer() {

		model = new ModelRocketTurret();
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
	    //TileEntityRenderer.instance.renderTileEntityAt(new RocketTurretTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}
}

package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelMachineGun;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class MachineGunTurretItemRenderer implements IItemRenderer {

	private ModelMachineGun model;

	public MachineGunTurretItemRenderer() {

		model = new ModelMachineGun();
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
		//TileEntityRenderer.instance.renderTileEntityAt(new MachineGunTurretTileEntity(), 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

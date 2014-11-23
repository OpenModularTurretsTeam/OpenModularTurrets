package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelDisposableItemTurret;
import modularTurrets.models.renderers.DisposableItemTurretRenderer;
import modularTurrets.tileentity.turrets.DisposableItemTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class DisposableItemTurretItemRenderer implements IItemRenderer {

    private final DisposableItemTurretRenderer disposableItemTurretRenderer;
    private final DisposableItemTurretTileEntity disposableItemTurretTileEntity;
    private final ModelDisposableItemTurret model;

	public DisposableItemTurretItemRenderer(DisposableItemTurretRenderer disposableItemTurretRenderer, DisposableItemTurretTileEntity disposableItemTurretTileEntity) {
        this.disposableItemTurretRenderer = disposableItemTurretRenderer;
        this.disposableItemTurretTileEntity = disposableItemTurretTileEntity;

		this.model = new ModelDisposableItemTurret();
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
	    this.disposableItemTurretRenderer.renderTileEntityAt(this.disposableItemTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

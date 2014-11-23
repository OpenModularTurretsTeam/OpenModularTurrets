package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelRocketTurret;
import modularTurrets.models.renderers.RocketTurretRenderer;
import modularTurrets.tileentity.turrets.RocketTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class RocketTurretItemRenderer implements IItemRenderer {

    private final RocketTurretRenderer rocketTurretRenderer;
    private final RocketTurretTileEntity rocketTurretTileEntity;
    private final ModelRocketTurret model;

    public RocketTurretItemRenderer(RocketTurretRenderer rocketTurretRenderer, RocketTurretTileEntity rocketTurretTileEntity) {
        this.rocketTurretRenderer = rocketTurretRenderer;
        this.rocketTurretTileEntity = rocketTurretTileEntity;

		this.model = new ModelRocketTurret();
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
        this.rocketTurretRenderer.renderTileEntityAt(this.rocketTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}
}

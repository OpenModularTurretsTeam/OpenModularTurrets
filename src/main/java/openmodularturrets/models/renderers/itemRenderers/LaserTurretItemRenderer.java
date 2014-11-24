package openmodularturrets.models.renderers.itemRenderers;

import openmodularturrets.models.ModelLaserTurret;
import openmodularturrets.models.renderers.LaserTurretRenderer;
import openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class LaserTurretItemRenderer implements IItemRenderer {

    private final LaserTurretRenderer laserTurretRenderer;
    private final LaserTurretTileEntity laserTurretTileEntity;
    private ModelLaserTurret model;

	public LaserTurretItemRenderer(LaserTurretRenderer laserTurretRenderer, LaserTurretTileEntity laserTurretTileEntity) {
        this.laserTurretRenderer = laserTurretRenderer;
        this.laserTurretTileEntity = laserTurretTileEntity;

        this.model = new ModelLaserTurret();
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
        this.laserTurretRenderer.renderTileEntityAt(this.laserTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

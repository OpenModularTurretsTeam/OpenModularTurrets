package modularTurrets.models.renderers.itemRenderers;

import modularTurrets.models.ModelGrenadeLauncher;
import modularTurrets.models.renderers.GrenadeLauncherTurretRenderer;
import modularTurrets.tileentity.turrets.GrenadeLauncherTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class GrenadeLauncherTurretItemRenderer implements IItemRenderer {

    private final GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer;
    private final GrenadeLauncherTurretTileEntity grenadeLauncherTurretTileEntity;
    private ModelGrenadeLauncher model;

	public GrenadeLauncherTurretItemRenderer(GrenadeLauncherTurretRenderer grenadeLauncherTurretRenderer, GrenadeLauncherTurretTileEntity grenadeLauncherTurretTileEntity) {
        this.grenadeLauncherTurretRenderer = grenadeLauncherTurretRenderer;
        this.grenadeLauncherTurretTileEntity = grenadeLauncherTurretTileEntity;

		this.model = new ModelGrenadeLauncher();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        this.grenadeLauncherTurretRenderer.renderTileEntityAt(this.grenadeLauncherTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

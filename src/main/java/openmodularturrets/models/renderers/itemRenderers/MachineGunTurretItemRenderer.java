package openmodularturrets.models.renderers.itemRenderers;

import openmodularturrets.models.ModelMachineGun;
import openmodularturrets.models.renderers.MachineGunTurretRenderer;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class MachineGunTurretItemRenderer implements IItemRenderer {

    private final MachineGunTurretRenderer machineGunTurretRenderer;
    private final MachineGunTurretTileEntity machineGunTurretTileEntity;
    private final ModelMachineGun model;

	public MachineGunTurretItemRenderer(MachineGunTurretRenderer machineGunTurretRenderer, MachineGunTurretTileEntity machineGunTurretTileEntity) {
        this.machineGunTurretRenderer = machineGunTurretRenderer;
        this.machineGunTurretTileEntity = machineGunTurretTileEntity;

		this.model = new ModelMachineGun();
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
        this.machineGunTurretRenderer.renderTileEntityAt(this.machineGunTurretTileEntity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

}

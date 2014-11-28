package openmodularturrets.client.render.renderers.items;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import openmodularturrets.client.render.models.ModelMachineGun;
import openmodularturrets.client.render.renderers.blockitem.MachineGunTurretRenderer;
import openmodularturrets.tileentity.turrets.MachineGunTurretTileEntity;

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

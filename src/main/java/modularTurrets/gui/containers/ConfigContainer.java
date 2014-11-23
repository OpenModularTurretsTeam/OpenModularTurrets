package modularTurrets.gui.containers;

import modularTurrets.tileEntities.turretBase.TurretBase;
import modularTurrets.tileEntities.turretBase.TurretWoodBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ConfigContainer extends Container {

	protected TurretBase tileEntity;

	public ConfigContainer(InventoryPlayer inventoryPlayer,
			TurretBase te) {
		this.tileEntity = te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
}
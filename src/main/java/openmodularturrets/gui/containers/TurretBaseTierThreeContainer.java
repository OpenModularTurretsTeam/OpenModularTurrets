package openmodularturrets.gui.containers;

import openmodularturrets.gui.customSlot.AddonSlot;
import openmodularturrets.gui.customSlot.UpgradeSlot;
import openmodularturrets.tileentity.turretBase.TurretBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TurretBaseTierThreeContainer extends Container {

	protected TurretBase tileEntity;

	public TurretBaseTierThreeContainer(InventoryPlayer inventoryPlayer,
			TurretBase te) {
		this.tileEntity = te;
		int i;
		int j;

		for (int x = 0; x < 9; x++) {
			this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18,
					142));
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlotToContainer(new Slot(inventoryPlayer,
						9 + x + y * 9, 8 + x * 18, 84 + y * 18));
			}
		}

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlotToContainer(new Slot(tileEntity, x + y * 3, 8 + x * 18,
						17 + y * 18));
			}
		}
		
		addSlotToContainer(new AddonSlot(tileEntity, 9, 72, 18));
		addSlotToContainer(new AddonSlot(tileEntity, 10, 92, 18));
		addSlotToContainer(new UpgradeSlot(tileEntity, 11, 72, 52));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			if (stackInSlot.stackSize <= 0) {
				slotObject.putStack(null);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
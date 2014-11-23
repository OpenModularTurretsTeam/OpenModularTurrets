package cofh.gui.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cofh.util.EnergyHelper;

public class SlotEnergy extends Slot {

	public SlotEnergy(IInventory inventory, int x, int y, int z) {

		super(inventory, x, y, z);
	}

	@Override
	public boolean isItemValid(ItemStack stack) {

		return EnergyHelper.isEnergyContainerItem(stack);
	}

}

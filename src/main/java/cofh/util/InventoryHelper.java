package cofh.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cofh.api.transport.IItemConduit;

public class InventoryHelper {

	private InventoryHelper() {

	}

	/**
	 * Copy an entire inventory. Best to avoid doing this often.
	 */
	public static ItemStack[] cloneInventory(ItemStack[] inventory) {

		ItemStack[] inventoryCopy = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			inventoryCopy[i] = inventory[i] == null ? null : inventory[i].copy();
		}
		return inventoryCopy;
	}

	/**
	 * Add an ItemStack to an inventory. Return true if the entire stack was added.
	 * 
	 * @param inventory
	 *            The inventory.
	 * @param stack
	 *            ItemStack to add.
	 * @param startIndex
	 *            First slot to attempt to add into. Does not loop around fully.
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack, int startIndex) {

		if (stack == null) {
			return true;
		}
		int openSlot = -1;
		for (int i = startIndex; i < inventory.length; i++) {
			if (ItemHelper.areItemStacksEqualNoNBT(stack, inventory[i]) && inventory[i].getMaxStackSize() > inventory[i].stackSize) {
				int hold = inventory[i].getMaxStackSize() - inventory[i].stackSize;
				if (hold >= stack.stackSize) {
					inventory[i].stackSize += stack.stackSize;
					stack = null;
					return true;
				} else {
					stack.stackSize -= hold;
					inventory[i].stackSize += hold;
				}
			} else if (inventory[i] == null && openSlot == -1) {
				openSlot = i;
			}
		}
		if (stack != null) {
			if (openSlot > -1) {
				inventory[openSlot] = stack;
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Shortcut method for above, assumes starting slot is 0.
	 */
	public static boolean addItemStackToInventory(ItemStack[] inventory, ItemStack stack) {

		return addItemStackToInventory(inventory, stack, 0);
	}

	/* IInventoryHandler Interaction */

	/* IIInventory Interaction */
	public static ItemStack extractItemStackFromInventory(IInventory theInventory, int side) {

		ItemStack retStack = null;

		if (theInventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) theInventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);
			for (int i = 0; i < slots.length && retStack == null; i++) {
				if (sidedInv.getStackInSlot(i) != null && sidedInv.canExtractItem(i, sidedInv.getStackInSlot(i), side)) {
					retStack = sidedInv.getStackInSlot(i).copy();
					sidedInv.setInventorySlotContents(i, null);
				}
			}
		} else {
			for (int i = 0; i < theInventory.getSizeInventory() && retStack == null; i++) {
				if (theInventory.getStackInSlot(i) != null) {
					retStack = theInventory.getStackInSlot(i).copy();
					theInventory.setInventorySlotContents(i, null);
				}
			}
		}
		if (retStack != null) {
			theInventory.onInventoryChanged();
		}
		return retStack;
	}

	public static ItemStack insertItemStackIntoInventory(IInventory theInventory, ItemStack stack, int side) {

		if (stack == null) {
			return null;
		}
		int stackSize = stack.stackSize;

		if (theInventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) theInventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side) && ItemHelper.itemsEqualWithMetadata(stack, theInventory.getStackInSlot(slots[i]), true)) {
					stack = addToOccupiedInventorySlot(sidedInv, slots[i], stack);
				}
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side) && theInventory.getStackInSlot(slots[i]) == null) {
					stack = addToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = theInventory.getSizeInventory();
			for (int i = 0; i < invSize && stack != null; i++) {
				if (ItemHelper.itemsEqualWithMetadata(stack, theInventory.getStackInSlot(i), true)) {
					stack = addToOccupiedInventorySlot(theInventory, i, stack);
				}
			}
			for (int i = 0; i < invSize && stack != null; i++) {
				if (theInventory.getStackInSlot(i) == null) {
					stack = addToEmptyInventorySlot(theInventory, i, stack);
				}
			}
		}
		if (stack == null || stack.stackSize != stackSize) {
			theInventory.onInventoryChanged();
		}
		return stack;
	}

	public static ItemStack simulateInsertItemStackIntoInventory(IInventory theInventory, ItemStack stack, int side) {

		if (stack == null) {
			return null;
		}
		if (theInventory instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) theInventory;
			int slots[] = sidedInv.getAccessibleSlotsFromSide(side);

			if (slots == null) {
				return stack;
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side) && ItemHelper.itemsEqualWithMetadata(stack, theInventory.getStackInSlot(slots[i]), true)) {
					stack = simulateAddToOccupiedInventorySlot(sidedInv, slots[i], stack);
				}
			}
			for (int i = 0; i < slots.length && stack != null; i++) {
				if (sidedInv.canInsertItem(slots[i], stack, side) && theInventory.getStackInSlot(slots[i]) == null) {
					stack = simulateAddToEmptyInventorySlot(sidedInv, slots[i], stack);
				}
			}
		} else {
			int invSize = theInventory.getSizeInventory();
			for (int i = 0; i < invSize && stack != null; i++) {
				if (ItemHelper.itemsEqualWithMetadata(stack, theInventory.getStackInSlot(i), true)) {
					stack = simulateAddToOccupiedInventorySlot(theInventory, i, stack);
				}
			}
			for (int i = 0; i < invSize && stack != null; i++) {
				if (theInventory.getStackInSlot(i) == null) {
					stack = simulateAddToEmptyInventorySlot(theInventory, i, stack);
				}
			}
		}
		return stack;
	}

	/* Slot Interaction */
	public static ItemStack addToEmptyInventorySlot(IInventory theInventory, int slot, ItemStack stack) {

		if (!theInventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = theInventory.getInventoryStackLimit();
		theInventory.setInventorySlotContents(slot, ItemHelper.cloneStack(stack, Math.min(stack.stackSize, stackLimit)));
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack addToOccupiedInventorySlot(IInventory theInventory, int slot, ItemStack stack) {

		ItemStack stackInSlot = theInventory.getStackInSlot(slot);
		int stackLimit = Math.min(theInventory.getInventoryStackLimit(), stackInSlot.getMaxStackSize());

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			int stackDiff = stackLimit - stackInSlot.stackSize;
			stackInSlot.stackSize = stackLimit;
			stack.stackSize -= stackDiff;
			theInventory.setInventorySlotContents(slot, stackInSlot);
			return stack;
		}
		stackInSlot.stackSize += Math.min(stack.stackSize, stackLimit);
		theInventory.setInventorySlotContents(slot, stackInSlot);
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToEmptyInventorySlot(IInventory theInventory, int slot, ItemStack stack) {

		if (!theInventory.isItemValidForSlot(slot, stack)) {
			return stack;
		}
		int stackLimit = theInventory.getInventoryStackLimit();
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	public static ItemStack simulateAddToOccupiedInventorySlot(IInventory theInventory, int slot, ItemStack stack) {

		ItemStack stackInSlot = theInventory.getStackInSlot(slot);
		int stackLimit = Math.min(theInventory.getInventoryStackLimit(), stackInSlot.getMaxStackSize());

		if (stack.stackSize + stackInSlot.stackSize > stackLimit) {
			stack.stackSize -= stackLimit - stackInSlot.stackSize;
			return stack;
		}
		return stackLimit >= stack.stackSize ? null : stack.splitStack(stack.stackSize - stackLimit);
	}

	/* HELPERS */
	public static ItemStack addToInsertion(Object theTile, int side, ItemStack stack) {

		if (stack == null) {
			return null;
		}
		if (theTile instanceof IInventory) {
			stack = insertItemStackIntoInventory((IInventory) theTile, stack, BlockHelper.SIDE_OPPOSITE[side]);
		} else {
			stack = ((IItemConduit) theTile).insertItem(ForgeDirection.VALID_DIRECTIONS[side ^ 1], stack);
		}
		return stack;
	}

	public static boolean isInventory(TileEntity theTile) {

		return theTile instanceof IInventory;
	}

	public static boolean isInsertion(Object theTile) {

		return theTile instanceof IInventory || theTile instanceof IItemConduit;
	}

}

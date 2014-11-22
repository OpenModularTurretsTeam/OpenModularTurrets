package cofh.util.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class InventoryCraftingCustom extends InventoryCrafting {

	public IInventory myMaster;
	public int invOffset = 0;
	public int invSize = 0;
	/** the width of the crafting inventory */
	public final int inventoryWidth;

	/**
	 * Class containing the callbacks for the events on_GUIClosed and on_CraftMaxtrixChanged.
	 */
	public final Container eventHandler;

	public InventoryCraftingCustom(Container container, int rows, int columns, IInventory masterTile, int startingInventoryIndex) {

		super(container, rows, columns);
		invSize = rows * columns;
		this.eventHandler = container;
		this.inventoryWidth = rows;
		invOffset = startingInventoryIndex;
		myMaster = masterTile;
	}

	@Override
	public int getSizeInventory() {

		return invSize;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {

		return slot >= this.getSizeInventory() ? null : myMaster.getStackInSlot(invOffset + slot);
	}

	@Override
	public ItemStack getStackInRowAndColumn(int row, int column) {

		if (row >= 0 && row < this.inventoryWidth) {
			int k = row + column * this.inventoryWidth;
			return this.getStackInSlot(k);
		} else {
			return null;
		}
	}

	@Override
	public String getInvName() {

		return "container.crafting";
	}

	@Override
	public boolean isInvNameLocalized() {

		return false;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {

		if (myMaster.getStackInSlot(invOffset + slot) != null) {
			ItemStack itemstack = myMaster.getStackInSlot(invOffset + slot);
			myMaster.setInventorySlotContents(invOffset + slot, null);
			return itemstack;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {

		if (myMaster.getStackInSlot(invOffset + slot) != null) {
			ItemStack itemstack;

			if (myMaster.getStackInSlot(invOffset + slot).stackSize <= amount) {
				itemstack = myMaster.getStackInSlot(invOffset + slot);
				myMaster.setInventorySlotContents(invOffset + slot, null);
				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			} else {
				itemstack = myMaster.getStackInSlot(invOffset + slot).splitStack(amount);

				if (myMaster.getStackInSlot(invOffset + slot).stackSize <= 0) {
					myMaster.setInventorySlotContents(invOffset + slot, null);
				}

				this.eventHandler.onCraftMatrixChanged(this);
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {

		myMaster.setInventorySlotContents(invOffset + slot, stack);
		this.eventHandler.onCraftMatrixChanged(this);
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void onInventoryChanged() {

	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openChest() {

	}

	@Override
	public void closeChest() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {

		return true;
	}

}

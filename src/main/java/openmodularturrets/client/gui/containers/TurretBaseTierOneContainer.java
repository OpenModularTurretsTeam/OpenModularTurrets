package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.tileentity.turretbase.TurretBaseTierOneTileEntity;

public class TurretBaseTierOneContainer extends Container {
    protected TurretBase tileEntity;

    public TurretBaseTierOneContainer(InventoryPlayer inventoryPlayer, TurretBaseTierOneTileEntity te) {
        this.tileEntity = te;

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new Slot(tileEntity, x + y * 3, 8 + x * 18, 17 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i) {
        Slot slot = getSlot(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack = slot.getStack();
            ItemStack result = itemstack.copy();

            if (i >= 36) {
                if (!mergeItemStack(itemstack, 0, 36, false)) {
                    return null;
                }
            } else if (!mergeItemStack(itemstack, 36, 36 + tileEntity.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            slot.onPickupFromSlot(player, itemstack);
            return result;
        }
        return null;
    }
}
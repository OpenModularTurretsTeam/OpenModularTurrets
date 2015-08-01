package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmodularturrets.client.gui.customSlot.AddonSlot;
import openmodularturrets.client.gui.customSlot.UpgradeSlot;
import openmodularturrets.items.addons.AddonItem;
import openmodularturrets.items.upgrades.UpgradeItem;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class TurretBaseTierFourContainer extends Container {

    protected TurretBase tileEntity;

    public TurretBaseTierFourContainer(InventoryPlayer inventoryPlayer, TurretBase te) {
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

            // Actual slot start index of turret base
            int slotStart = 36;

            int ammoSlotStart = 100, ammoSlotEnd = 0, addonSlotStart = 100, addonSlotEnd = 0, upgSlotStart = 100, upgSlotEnd = 0;
            // Determine the slot range for each type( According to the class constructor )
            for (int i = slotStart; i < this.inventorySlots.size(); i++) {
                Class slotClass = this.getSlot(i).getClass();
                if (slotClass == Slot.class) {
                    ammoSlotStart = Math.min(i, ammoSlotStart);
                    ammoSlotEnd = Math.max(i + 1, ammoSlotEnd);
                } else if (slotClass == AddonSlot.class) {
                    addonSlotStart = Math.min(i, addonSlotStart);
                    addonSlotEnd = Math.max(i + 1, addonSlotEnd);
                } else if (slotClass == UpgradeSlot.class) {
                    upgSlotStart = Math.min(i, upgSlotStart);
                    upgSlotEnd = Math.max(i + 1, upgSlotEnd);
                }
            }

            // Transfer from player inventory
            if (slot < slotStart) {
                // Priority addon and upgrade slot first
                if (stackInSlot.getItem() instanceof AddonItem) {
                    if (!mergeItemStack(stackInSlot, addonSlotStart, addonSlotEnd, false)) {
                        return null;
                    }
                } else if (stackInSlot.getItem() instanceof UpgradeItem) {
                    if (!mergeItemStack(stackInSlot, upgSlotStart, upgSlotEnd, false)) {
                        return null;
                    }
                } else {
                    if (!mergeItemStack(stackInSlot, slotStart, slotStart + 9, false)) {
                        return null;
                    }
                }
            } else // Transfer from turret base inventory
            {
                if (!mergeItemStack(stackInSlot, 0, slotStart, false)) {
                    return null;
                }
            }

            if (stackInSlot.stackSize == 0) {
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
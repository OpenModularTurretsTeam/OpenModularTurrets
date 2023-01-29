package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import openmodularturrets.client.gui.customSlot.AddonSlot;
import openmodularturrets.client.gui.customSlot.UpgradeSlot;
import openmodularturrets.handler.NetworkingHandler;
import openmodularturrets.items.addons.AddonItem;
import openmodularturrets.items.upgrades.UpgradeItem;
import openmodularturrets.network.messages.MessageTurretBase;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by Keridos on 09/12/2015. This Class
 */
class TurretBaseContainer extends Container {

    TurretBase tileEntity;

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

            int ammoSlotStart = 100, ammoSlotEnd = 0, addonSlotStart = 100, addonSlotEnd = 0, upgSlotStart = 100,
                    upgSlotEnd = 0;
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

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (Object crafter : crafters) {
            if (crafter instanceof EntityPlayerMP) {
                NetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this.tileEntity), (EntityPlayerMP) crafter);
            }
        }
    }
}

package omtteam.openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import omtteam.omlib.compatability.minecraft.CompatSlot;
import omtteam.openmodularturrets.client.gui.customSlot.AddonSlot;
import omtteam.openmodularturrets.client.gui.customSlot.UpgradeSlot;
import omtteam.openmodularturrets.handler.NetworkingHandler;
import omtteam.openmodularturrets.items.AddonMetaItem;
import omtteam.openmodularturrets.items.UpgradeMetaItem;
import omtteam.openmodularturrets.network.messages.MessageTurretBase;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.InvUtil.mergeItemStackWithStackLimit;
import static omtteam.omlib.util.compat.ItemStackTools.getStackSize;

/**
 * Created by Keridos on 09/12/2015.
 * This Class
 */
public abstract class TurretBaseContainer extends Container {
    TurretBase tileEntity;

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUsableByPlayer(player);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        CompatSlot slotObject = (CompatSlot) inventorySlots.get(slot);

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
                if (stackInSlot.getItem() instanceof AddonMetaItem) {
                    if (!mergeItemStackWithStackLimit(stackInSlot, addonSlotStart, addonSlotEnd, false, this)) {
                        return null;
                    }
                } else if (stackInSlot.getItem() instanceof UpgradeMetaItem) {
                    if (!mergeItemStackWithStackLimit(stackInSlot, upgSlotStart, upgSlotEnd, false, this)) {
                        return null;
                    }
                } else {
                    if (!mergeItemStackWithStackLimit(stackInSlot, slotStart, slotStart + 9, false, this)) {
                        return null;
                    }
                }
            } else // Transfer from turret base inventory
            {
                if (!mergeItemStackWithStackLimit(stackInSlot, 0, slotStart, false, this)) {
                    return null;
                }
            }

            if (getStackSize(stackInSlot) == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }

            if (getStackSize(stackInSlot) == getStackSize(stack)) {
                return null;
            }
            slotObject.onPickup(player, stackInSlot);
        }
        return stack;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : this.listeners) {
            if (listener instanceof EntityPlayerMP) {
                NetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this.tileEntity), (EntityPlayerMP) listener);
            }
        }
    }
}

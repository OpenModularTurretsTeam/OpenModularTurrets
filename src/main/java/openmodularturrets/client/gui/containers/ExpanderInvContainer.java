package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmodularturrets.tileentity.Expander;

public class ExpanderInvContainer extends Container {
    private final Expander tileEntity;

    public ExpanderInvContainer(InventoryPlayer inventoryPlayer, Expander te) {
        this.tileEntity = te;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlotToContainer(new Slot(te, y + x * 3, 62 + y * 18, 17 + x * 18));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slot) {
        ItemStack itemStack = null;
        Slot invSlot = this.inventorySlots.get(slot);

        if (invSlot != null && invSlot.getHasStack()) {
            ItemStack itemStack1 = invSlot.getStack();
            itemStack = itemStack1.copy();

            if (slot < 9) {
                if (!this.mergeItemStack(itemStack1, 9, 45, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemStack1, 0, 9, false)) {
                return null;
            }

            if (itemStack1.stackSize == 0) {
                invSlot.putStack(null);
            } else {
                invSlot.onSlotChanged();
            }

            if (itemStack1.stackSize == itemStack.stackSize) {
                return null;
            }

            invSlot.onPickupFromSlot(playerIn, itemStack1);
        }

        return itemStack;
    }

    @Override
    public boolean mergeItemStack(ItemStack stack, int begin, int end, boolean backwards) {
        int i = backwards ? end - 1 : begin, increment = backwards ? -1 : 1;
        boolean flag = false;
        while (stack.stackSize > 0 && i >= begin && i < end) {
            Slot slot = this.getSlot(i);
            ItemStack slotStack = slot.getStack();
            int slotStackLimit = i < tileEntity.getSizeInventory() ? tileEntity.getInventoryStackLimit() : 64;
            int totalLimit = slotStackLimit < stack.getMaxStackSize() ? slotStackLimit : stack.getMaxStackSize();

            if (slotStack == null) {
                int transfer = totalLimit < stack.stackSize ? totalLimit : stack.stackSize;
                ItemStack stackToPut = stack.copy();
                stackToPut.stackSize = transfer;
                slot.putStack(stackToPut);
                slot.onSlotChanged();
                stack.stackSize -= transfer;
                flag = true;
            } else if (slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(
                    stack, slotStack)) {
                int maxTransfer = totalLimit - slotStack.stackSize;
                int transfer = maxTransfer > stack.stackSize ? stack.stackSize : maxTransfer;
                slotStack.stackSize += transfer;
                slot.onSlotChanged();
                stack.stackSize -= transfer;
                flag = true;
            }

            i += increment;
        }

        return flag;
    }
}
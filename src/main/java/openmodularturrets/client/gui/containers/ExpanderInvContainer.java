package openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import openmodularturrets.tileentity.expander.AbstractInvExpander;

public class ExpanderInvContainer extends Container {
    private final AbstractInvExpander tileEntity;

    public ExpanderInvContainer(InventoryPlayer inventoryPlayer, AbstractInvExpander te) {
        this.tileEntity = te;

        int i;
        int j;

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(te, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntity.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ < 9) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(p_82846_1_, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean mergeItemStack(ItemStack stack, int begin, int end, boolean backwards) {
        int i = backwards ? end - 1 : begin, increment = backwards ? -1 : 1;
        boolean flag = false;
        while (stack.stackSize > 0 && i >= begin && i < end) {
            Slot slot = this.getSlot(i);
            ItemStack slotStack = slot.getStack();
            int slotStacklimit = i < tileEntity.getSizeInventory() ? tileEntity.getInventoryStackLimit() : 64;
            int totalLimit = slotStacklimit < stack.getMaxStackSize() ? slotStacklimit : stack.getMaxStackSize();

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
package omtteam.openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.client.gui.slot.AmmoSlot;
import omtteam.openmodularturrets.tileentity.Expander;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.inventory.InvUtil.*;

public class ExpanderInvContainer extends Container {
    private final Expander tileEntity;

    public ExpanderInvContainer(InventoryPlayer inventoryPlayer, Expander te) {
        this.tileEntity = te;

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                this.addSlotToContainer(new AmmoSlot(te.getInventory(), y + x * 3, 62 + y * 18, 17 + x * 18));
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
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq(this.tileEntity.getPos().getX() + 0.5,
                                    this.tileEntity.getPos().getY() + 0.5,
                                    this.tileEntity.getPos().getZ() + 0.5) < 64;
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot invSlot = this.inventorySlots.get(slot);

        if (invSlot != null && invSlot.getHasStack()) {
            ItemStack itemStack1 = invSlot.getStack();
            itemStack = itemStack1.copy();

            if (slot < 9) {
                if (!mergeItemStackWithStackLimit(itemStack1, 9, 45, true, this)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStackWithStackLimit(itemStack1, 0, 9, false, this)) {
                return ItemStack.EMPTY;
            }

            if (getStackSize(itemStack1) == 0) {
                invSlot.putStack(ItemStack.EMPTY);
            } else {
                invSlot.onSlotChanged();
            }

            if (getStackSize(itemStack1) == getStackSize(itemStack)) {
                return ItemStack.EMPTY;
            }

            invSlot.onSlotChanged();
        }

        return itemStack;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean mergeItemStack(ItemStack stack, int begin, int end, boolean backwards) {
        int i = backwards ? end - 1 : begin, increment = backwards ? -1 : 1;
        boolean flag = false;
        while (getStackSize(stack) > 0 && i >= begin && i < end) {
            Slot slot = this.getSlot(i);
            ItemStack slotStack = slot.getStack();
            int slotStackLimit = i < tileEntity.getInventory().getSlots() ? tileEntity.getInventory().getSlotLimit(1) : 64;
            int totalLimit = slotStackLimit < stack.getMaxStackSize() ? slotStackLimit : stack.getMaxStackSize();

            if (slotStack == ItemStack.EMPTY) {
                int transfer = totalLimit < getStackSize(stack) ? totalLimit : getStackSize(stack);
                ItemStack stackToPut = stack.copy();
                setStackSize(stackToPut, transfer);
                slot.putStack(stackToPut);
                slot.onSlotChanged();
                setStackSize(stack, getStackSize(stack) - transfer);
                flag = true;
            } else if (slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(
                    stack, slotStack)) {
                int maxTransfer = totalLimit - getStackSize(slotStack);
                int transfer = maxTransfer > getStackSize(stack) ? getStackSize(stack) : maxTransfer;
                slotStack.setCount(slotStack.getCount() + transfer);
                slot.onSlotChanged();
                setStackSize(stack, getStackSize(stack) - transfer);
                flag = true;
            }

            i += increment;
        }

        return flag;
    }
}
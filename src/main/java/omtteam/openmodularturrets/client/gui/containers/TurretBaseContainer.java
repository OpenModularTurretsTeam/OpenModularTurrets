package omtteam.openmodularturrets.client.gui.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import omtteam.omlib.util.DebugHandler;
import omtteam.openmodularturrets.client.gui.slot.AddonSlot;
import omtteam.openmodularturrets.client.gui.slot.AmmoSlot;
import omtteam.openmodularturrets.client.gui.slot.UpgradeSlot;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.items.AddonMetaItem;
import omtteam.openmodularturrets.items.UpgradeMetaItem;
import omtteam.openmodularturrets.network.messages.MessageTurretBase;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.EnumSlotType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.inventory.InvUtil.getStackSize;
import static omtteam.omlib.util.inventory.InvUtil.mergeItemStackWithStackLimit;

/**
 * Created by Keridos on 09/12/2015.
 * This Class
 */
public class TurretBaseContainer extends Container {
    TurretBase base;
    // Actual slot start index of turret base
    int slotStart = 36;
    int ammoSlotStart = 100, ammoSlotEnd = 0, addonSlotStart = 100, addonSlotEnd = 0, upgSlotStart = 100, upgSlotEnd = 0;

    public TurretBaseContainer(InventoryPlayer inventoryPlayer, TurretBase turretBase) {
        this.base = turretBase;

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(inventoryPlayer, x, 8 + x * 18, 142));
        }

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(inventoryPlayer, 9 + x + y * 9, 8 + x * 18, 84 + y * 18));
            }
        }
        // TODO: maybe make this support more than 9 ammo slots.
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlotToContainer(new AmmoSlot(base.getInventory(), x + y * 3, 8 + x * 18, 17 + y * 18));
            }
        }
        if (base.getTier() > 1) {
            addSlotToContainer(new AddonSlot(base.getInventory(), base.getSlotMap().get(EnumSlotType.AddonSlot).get(0), 72, 18));
            addSlotToContainer(new AddonSlot(base.getInventory(), base.getSlotMap().get(EnumSlotType.AddonSlot).get(1), 92, 18));
            addSlotToContainer(new UpgradeSlot(base.getInventory(), base.getSlotMap().get(EnumSlotType.UpgradeSlot).get(0), 72, 52));
        }
        if (base.getTier() > 4) {
            addSlotToContainer(new UpgradeSlot(base.getInventory(), base.getSlotMap().get(EnumSlotType.UpgradeSlot).get(1), 92, 52));
        }

        // Determine the slot range for each type( According to the class constructor )
        for (int i = slotStart; i < this.inventorySlots.size(); i++) {
            Class slotClass = this.getSlot(i).getClass();
            if (slotClass == AmmoSlot.class) {
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
    }

    @Override
    @ParametersAreNonnullByDefault
    public boolean canInteractWith(EntityPlayer player) {
        return player.getDistanceSq(this.base.getPos().getX() + 0.5,
                                    this.base.getPos().getY() + 0.5,
                                    this.base.getPos().getZ() + 0.5) < 64;
    }

    @SuppressWarnings("ConstantConditions")
    @ParametersAreNonnullByDefault
    @Override
    @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slotObject = inventorySlots.get(slot);

        // null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            // Transfer from player inventory
            if (slot < slotStart) {
                // Priority addon and upgrade slot first
                if (stackInSlot.getItem() instanceof AddonMetaItem) {
                    if (!mergeItemStackWithStackLimit(stackInSlot, addonSlotStart, addonSlotEnd, false, this)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stackInSlot.getItem() instanceof UpgradeMetaItem) {
                    if (!mergeItemStackWithStackLimit(stackInSlot, upgSlotStart, upgSlotEnd, false, this)) {
                        base.updateMaxRange();
                        base.turretResetCaches();
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!mergeItemStackWithStackLimit(stackInSlot, slotStart, slotStart + 9, false, this)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else // Transfer from turret base inventory
            {
                if (!mergeItemStackWithStackLimit(stackInSlot, 0, slotStart, false, this)) {
                    slotObject.onSlotChanged();
                    base.updateMaxRange();
                    base.turretResetCaches();
                    return ItemStack.EMPTY;
                }
            }

            if (getStackSize(stackInSlot) == 0) {
                slotObject.putStack(ItemStack.EMPTY);
            } else {
                slotObject.onSlotChanged();
            }
            base.updateMaxRange();
            base.turretResetCaches();

            if (getStackSize(stackInSlot) == getStackSize(stack)) {
                slotObject.onSlotChanged();
                return ItemStack.EMPTY;
            }
            slotObject.onSlotChanged();
        }
        return stack;
    }

    @Override
    public void putStackInSlot(int slotID, ItemStack stack) {
        super.putStackInSlot(slotID, stack);
        if (slotID >= upgSlotStart && slotID <= upgSlotEnd) {
            base.updateMaxRange();
            base.turretResetCaches();
        }
    }

    @Override
    public void detectAndSendChanges() {
        DebugHandler.getInstance().setListeners(this.listeners);
        for (IContainerListener listener : this.listeners) {
            if (listener instanceof EntityPlayerMP) {
                OMTNetworkingHandler.INSTANCE.sendTo(new MessageTurretBase(this.base), (EntityPlayerMP) listener);
            }
        }
        base.updateMaxRange();
        base.turretResetCaches();
        super.detectAndSendChanges();
    }
}

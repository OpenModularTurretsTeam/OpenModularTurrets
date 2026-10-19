package omtteam.openmodularturrets.platform.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.inventory.ItemAccess;

import java.util.List;
import java.util.function.Predicate;

/**
 * Fabric Transfer API view of an {@link ItemAccess}: Fabric's {@link InventoryStorage} (transactions, rollback),
 * limited to the slot range and filtered. It stays slotted, so mods that read inventories slot by slot (Jade) still see
 * the contents.
 */
public final class FabricItemStorages {
    private FabricItemStorages() {
    }

    public static SlottedStorage<ItemVariant> of(ItemAccess access) {
        List<SingleSlotStorage<ItemVariant>> slots = InventoryStorage.of(access.container(), null).getSlots()
                .subList(access.firstSlot(), access.firstSlot() + access.slotCount());
        return new CombinedSlottedStorage<>(slots.stream()
                .<SingleSlotStorage<ItemVariant>>map(slot -> new FilteredSlot(slot, access.canInsert(), access.canExtract()))
                .toList());
    }

    private record FilteredSlot(SingleSlotStorage<ItemVariant> slot, Predicate<ItemStack> canInsert,
                                Predicate<ItemStack> canExtract) implements SingleSlotStorage<ItemVariant> {
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canInsert.test(resource.toStack()) ? slot.insert(resource, maxAmount, transaction) : 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return canExtract.test(resource.toStack()) ? slot.extract(resource, maxAmount, transaction) : 0;
        }

        @Override
        public boolean isResourceBlank() {
            return slot.isResourceBlank();
        }

        @Override
        public ItemVariant getResource() {
            return slot.getResource();
        }

        @Override
        public long getAmount() {
            return slot.getAmount();
        }

        @Override
        public long getCapacity() {
            return slot.getCapacity();
        }
    }
}

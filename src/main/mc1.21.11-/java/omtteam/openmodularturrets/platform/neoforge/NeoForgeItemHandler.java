package omtteam.openmodularturrets.platform.neoforge;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import omtteam.openmodularturrets.inventory.ItemAccess;

/**
 * NeoForge view ({@code ResourceHandler<ItemResource>}) of an {@link ItemAccess}: NeoForge's container wrapper
 * (transactions, rollback), limited to the slot range and filtered.
 */
public final class NeoForgeItemHandler extends DelegatingResourceHandler<ItemResource> {
    private final ItemAccess access;

    public NeoForgeItemHandler(ItemAccess access) {
        super(RangedResourceHandler.of(VanillaContainerWrapper.of(access.container()), access.firstSlot(),
                access.firstSlot() + access.slotCount()));
        this.access = access;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        return canInsert(resource) && super.isValid(index, resource);
    }

    @Override
    public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return canInsert(resource) ? super.insert(index, resource, amount, transaction) : 0;
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        return canInsert(resource) ? super.insert(resource, amount, transaction) : 0;
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        return canExtract(resource) ? super.extract(index, resource, amount, transaction) : 0;
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return canExtract(resource) ? super.extract(resource, amount, transaction) : 0;
    }

    private boolean canInsert(ItemResource resource) {
        return !resource.isEmpty() && access.canInsert().test(resource.toStack());
    }

    private boolean canExtract(ItemResource resource) {
        return !resource.isEmpty() && access.canExtract().test(resource.toStack());
    }
}

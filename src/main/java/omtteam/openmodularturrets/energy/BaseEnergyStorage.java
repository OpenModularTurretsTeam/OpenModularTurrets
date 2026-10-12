package omtteam.openmodularturrets.energy;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import omtteam.openmodularturrets.core.base.EnergyStore;
import team.reborn.energy.api.EnergyStorage;

/**
 * Exposes a core {@link EnergyStore} through Team Reborn Energy, with transaction support. Insert-only, like the
 * oldmod base (it was a consumer only).
 */
public class BaseEnergyStorage extends SnapshotParticipant<Long> implements EnergyStorage {
    private final EnergyStore store;

    public BaseEnergyStorage(EnergyStore store) {
        this.store = store;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long inserted = store.insert(maxAmount, true);
        if (inserted > 0) {
            updateSnapshots(transaction);
            store.insert(inserted, false);
        }
        return inserted;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public long getAmount() {
        return store.getAmount();
    }

    @Override
    public long getCapacity() {
        return store.getCapacity();
    }

    @Override
    protected Long createSnapshot() {
        return store.getAmount();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        store.setAmountUnchecked(snapshot);
    }
}

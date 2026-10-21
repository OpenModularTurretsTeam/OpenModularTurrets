package omtteam.openmodularturrets.platform.fabric;

import com.google.common.collect.MapMaker;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.energy.EnergyStore;
import team.reborn.energy.api.EnergyStorage;

import java.util.Map;

/**
 * Team Reborn Energy views of {@link EnergyAccess}. A store gets one {@link SnapshotParticipant} for exact rollback in
 * transactions.
 */
public final class FabricEnergyStorages {
    /**
     * One adapter per store, so that a transaction snapshots each store once. Weak values: an adapter is kept alive by
     * the transactions it takes part in, and between transactions a new one is just as good (no snapshot is open).
     */
    private static final Map<EnergyStore, StoreStorage> STORES = new MapMaker().weakKeys().weakValues().makeMap();

    private FabricEnergyStorages() {
    }

    public static EnergyStorage of(EnergyAccess access) {
        if (access instanceof EnergyStore store) {
            return STORES.computeIfAbsent(store, StoreStorage::new);
        }
        throw new IllegalArgumentException("No Team Reborn Energy adapter for " + access.getClass());
    }

    private static final class StoreStorage extends SnapshotParticipant<Long> implements EnergyStorage {
        private final EnergyStore store;

        StoreStorage(EnergyStore store) {
            this.store = store;
        }

        @Override
        public boolean supportsInsertion() {
            return store.getMaxInsert() > 0;
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
            return store.getMaxExtract() > 0;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            StoragePreconditions.notNegative(maxAmount);
            long extracted = store.extract(maxAmount, true);
            if (extracted > 0) {
                updateSnapshots(transaction);
                store.extract(extracted, false);
            }
            return extracted;
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
}

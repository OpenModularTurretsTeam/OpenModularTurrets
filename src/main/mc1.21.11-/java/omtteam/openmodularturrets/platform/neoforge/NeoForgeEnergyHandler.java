package omtteam.openmodularturrets.platform.neoforge;

import com.google.common.collect.MapMaker;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.energy.EnergyStore;

import java.util.Map;

/**
 * NeoForge view ({@link EnergyHandler}, FE, {@code int}) of an {@link EnergyAccess}; 1 E = 1 FE, larger amounts are
 * clamped to {@link Integer#MAX_VALUE}. A store gets one {@link SnapshotJournal} for exact rollback in transactions
 * (like {@code FabricEnergyStorages}).
 */
public final class NeoForgeEnergyHandler {
    /**
     * One handler per store, so that a transaction snapshots each store once. Weak values: a handler is kept alive by
     * the transactions it takes part in, and between transactions a new one is just as good (no snapshot is open).
     */
    private static final Map<EnergyStore, StoreHandler> STORES = new MapMaker().weakKeys().weakValues().makeMap();

    private NeoForgeEnergyHandler() {
    }

    public static EnergyHandler of(EnergyAccess access) {
        if (access instanceof EnergyStore store) {
            return STORES.computeIfAbsent(store, StoreHandler::new);
        }
        throw new IllegalArgumentException("No NeoForge energy handler for " + access.getClass());
    }

    static int clamp(long amount) {
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0, amount));
    }

    private static final class StoreHandler extends SnapshotJournal<Long> implements EnergyHandler {
        private final EnergyStore store;

        StoreHandler(EnergyStore store) {
            this.store = store;
        }

        @Override
        public long getAmountAsLong() {
            return store.getAmount();
        }

        @Override
        public long getCapacityAsLong() {
            return store.getCapacity();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            int inserted = clamp(store.insert(amount, true));
            if (inserted > 0) {
                updateSnapshots(transaction);
                store.insert(inserted, false);
            }
            return inserted;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            int extracted = clamp(store.extract(amount, true));
            if (extracted > 0) {
                updateSnapshots(transaction);
                store.extract(extracted, false);
            }
            return extracted;
        }

        @Override
        protected Long createSnapshot() {
            return store.getAmount();
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            store.setAmountUnchecked(snapshot);
        }
    }
}

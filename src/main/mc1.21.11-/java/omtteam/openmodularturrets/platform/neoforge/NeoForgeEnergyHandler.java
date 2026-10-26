package omtteam.openmodularturrets.platform.neoforge;

import com.google.common.collect.MapMaker;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import omtteam.openmodularturrets.core.energy.EnergyAccess;
import omtteam.openmodularturrets.core.energy.EnergyGroup;
import omtteam.openmodularturrets.core.energy.EnergyStore;

import java.util.Map;

/**
 * NeoForge views ({@link EnergyHandler}, FE, {@code int}) of {@link EnergyAccess}; 1 E = 1 FE, larger amounts are
 * clamped to {@link Integer#MAX_VALUE}. A store gets one {@link SnapshotJournal} for exact rollback in transactions; a
 * group distributes into the handlers of its stores (like {@code FabricEnergyStorages}).
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
        if (access instanceof EnergyGroup group) {
            return new GroupHandler(group);
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

    /** Insert only: distributes through {@link EnergyGroup#fill}. */
    private static final class GroupHandler implements EnergyHandler {
        private final EnergyGroup group;

        GroupHandler(EnergyGroup group) {
            this.group = group;
        }

        @Override
        public long getAmountAsLong() {
            return group.getAmount();
        }

        @Override
        public long getCapacityAsLong() {
            return group.getCapacity();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            TransferPreconditions.checkNonNegative(amount);
            return clamp(group.fill(amount, (store, part) -> of(store).insert(clamp(part), transaction)));
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            return 0;
        }
    }
}

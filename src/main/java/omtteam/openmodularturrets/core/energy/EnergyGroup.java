package omtteam.openmodularturrets.core.energy;

/**
 * An {@link EnergyAccess} that spreads what is inserted over several {@link EnergyStore}s (blocks that share their
 * energy). The loader adapters insert through {@link #fill} with an inserter of their own for each store, so that each
 * store takes part in the loader's transaction with its own rollback.
 */
public interface EnergyGroup extends EnergyAccess {
    /** Inserts into one store of the group; the adapters pass their own (Fabric: inside a transaction). */
    @FunctionalInterface
    interface StoreInserter {
        long insert(EnergyStore store, long maxAmount);
    }

    /**
     * Inserts up to {@code maxAmount} into the stores of the group, each through {@code inserter}.
     *
     * @return the amount inserted
     */
    long fill(long maxAmount, StoreInserter inserter);
}

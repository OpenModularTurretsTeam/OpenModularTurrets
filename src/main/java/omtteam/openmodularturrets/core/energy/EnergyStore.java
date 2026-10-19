package omtteam.openmodularturrets.core.energy;

/**
 * Energy buffer of a block entity, free of Minecraft and loaders.
 * Other blocks go through {@link #insert} / {@link #extract}, limited by {@code maxInsert} / {@code maxExtract}; the
 * loader adapters expose these. The owner uses {@link #generate}, {@link #consume} and {@link #drain}, which ignore
 * those limits. Negative arguments count as 0.
 */
public final class EnergyStore implements EnergyAccess {
    private final Runnable onChange;
    private long amount;
    private long capacity;
    private long maxInsert;
    private long maxExtract;

    /**
     * @param onChange runs on every real change (not for simulations or {@link #setAmountUnchecked}); the owner marks
     *                 its block entity dirty there
     */
    public EnergyStore(long capacity, long maxInsert, long maxExtract, Runnable onChange) {
        this.capacity = Math.max(0, capacity);
        this.maxInsert = Math.max(0, maxInsert);
        this.maxExtract = Math.max(0, maxExtract);
        this.onChange = onChange;
    }

    @Override
    public long getAmount() {
        return amount;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    /** Room left; never negative, even after {@link #setAmountUnchecked} above the capacity. */
    public long getFreeSpace() {
        return Math.max(0, capacity - amount);
    }

    public long getMaxInsert() {
        return maxInsert;
    }

    public long getMaxExtract() {
        return maxExtract;
    }

    /** Lowers the stored energy if it exceeds the new capacity. */
    public void setCapacity(long capacity) {
        this.capacity = Math.max(0, capacity);
        if (amount > this.capacity) {
            amount = this.capacity;
            onChange.run();
        }
    }

    public void setMaxInsert(long maxInsert) {
        this.maxInsert = Math.max(0, maxInsert);
    }

    public void setMaxExtract(long maxExtract) {
        this.maxExtract = Math.max(0, maxExtract);
    }

    // ---- other blocks (via the loader adapters) ----

    /**
     * Inserts energy from outside, limited by {@code maxInsert} and the free space.
     *
     * @return the amount accepted (or that would be, if simulated)
     */
    @Override
    public long insert(long maxAmount, boolean simulate) {
        long inserted = Math.min(maxInsert, Math.min(Math.max(0, maxAmount), getFreeSpace()));
        if (!simulate && inserted > 0) {
            amount += inserted;
            onChange.run();
        }
        return inserted;
    }

    /**
     * Extracts energy for another block, limited by {@code maxExtract} and the stored amount.
     *
     * @return the amount extracted (or that would be, if simulated)
     */
    @Override
    public long extract(long maxAmount, boolean simulate) {
        long extracted = Math.min(maxExtract, Math.min(Math.max(0, maxAmount), amount));
        if (!simulate && extracted > 0) {
            amount -= extracted;
            onChange.run();
        }
        return extracted;
    }

    // ---- the owner ----

    /** Adds internally generated or shared energy up to the free space, ignoring {@code maxInsert}; returns what was added. */
    public long generate(long energy) {
        long added = Math.min(Math.max(0, energy), getFreeSpace());
        if (added > 0) {
            amount += added;
            onChange.run();
        }
        return added;
    }

    /** Removes {@code energy} if at least that much is stored (all or nothing). */
    public boolean consume(long energy) {
        if (energy < 0 || amount < energy) return false;
        if (energy > 0) {
            amount -= energy;
            onChange.run();
        }
        return true;
    }

    /** Removes up to {@code maxAmount}, ignoring {@code maxExtract}; returns what was removed. */
    public long drain(long maxAmount) {
        long drained = Math.min(Math.max(0, maxAmount), amount);
        if (drained > 0) {
            amount -= drained;
            onChange.run();
        }
        return drained;
    }

    /**
     * Sets the stored energy without clamping to the capacity and without {@code onChange}: for loading (before the
     * final capacity is known) and for the rollback of the Fabric adapter.
     */
    public void setAmountUnchecked(long amount) {
        this.amount = Math.max(0, amount);
    }
}

package omtteam.openmodularturrets.core.base;

/**
 * Energy buffer of a base. Replaces OMLib {@code OMEnergyStorage}. Externally it only accepts energy (at most
 * {@code maxInsert} per insertion); the base itself consumes and generates. The capacity changes with power expanders.
 */
public class EnergyStore {
    private final Runnable onChange;
    private long amount;
    private long capacity;
    private long maxInsert;

    public EnergyStore(long capacity, long maxInsert, Runnable onChange) {
        this.capacity = Math.max(0, capacity);
        this.maxInsert = Math.max(0, maxInsert);
        this.onChange = onChange;
    }

    public long getAmount() {
        return amount;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getMaxInsert() {
        return maxInsert;
    }

    public long getFreeSpace() {
        return Math.max(0, capacity - amount);
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

    /** Sets the stored energy without clamping (loading happens before the final capacity is known). */
    public void setAmountUnchecked(long amount) {
        this.amount = Math.max(0, amount);
    }

    /**
     * Inserts energy from outside, limited by free space and {@code maxInsert} (OMLib {@code receiveEnergy}).
     *
     * @return the amount accepted
     */
    public long insert(long maxAmount, boolean simulate) {
        long inserted = Math.min(maxInsert, Math.min(Math.max(0, maxAmount), getFreeSpace()));
        if (!simulate && inserted > 0) {
            amount += inserted;
            onChange.run();
        }
        return inserted;
    }

    /** Adds internally generated energy, ignoring {@code maxInsert} (OMLib {@code modifyEnergyStored}). */
    public long generate(long energy) {
        long added = Math.min(Math.max(0, energy), getFreeSpace());
        if (added > 0) {
            amount += added;
            onChange.run();
        }
        return added;
    }

    /** Removes {@code energy} if at least that much is stored. */
    public boolean consume(long energy) {
        if (energy < 0 || amount < energy) return false;
        if (energy > 0) {
            amount -= energy;
            onChange.run();
        }
        return true;
    }
}

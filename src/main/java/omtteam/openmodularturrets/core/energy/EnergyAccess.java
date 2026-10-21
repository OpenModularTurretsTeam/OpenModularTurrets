package omtteam.openmodularturrets.core.energy;

/**
 * Energy that a block entity exposes to other blocks, free of Minecraft and loaders: an {@link EnergyStore}, or a
 * view over several. The loader adapters in {@code platform/<loader>} turn it into the loader's
 * energy API. Negative arguments count as 0.
 */
public interface EnergyAccess {
    /** Energy stored now. */
    long getAmount();

    /** Most energy that can be stored. */
    long getCapacity();

    /** @return the amount accepted (or that would be, if simulated) */
    long insert(long maxAmount, boolean simulate);

    /** @return the amount extracted (or that would be, if simulated) */
    long extract(long maxAmount, boolean simulate);
}

package omtteam.openmodularturrets.core.generator;

import java.util.OptionalLong;

/**
 * Burning rules of the generator (new block, not in the 1.12.2 version): one item of fuel burns for its burn time,
 * and every burn tick produces the configured energy. Burning pauses while the energy buffer is full, so no fuel is
 * wasted. The block entity owns the fuel slot and the energy buffer and pushes the energy into its neighbours.
 */
public final class GeneratorLogic {
    /** The generator's fuel slot. */
    @FunctionalInterface
    public interface FuelSlot {
        /**
         * Starts burning the next fuel item: removes it from the slot (leaving its remainder, like a furnace).
         *
         * @return its burn time in ticks, 0 if the slot holds no fuel (nothing is removed then)
         */
        int consumeNext();
    }

    /** Ticks left of the fuel item burning now. */
    private int burnTime;
    /** Burn time of the fuel item burning now (for the flame in the GUI). */
    private int burnDuration;

    public int getBurnTime() {
        return burnTime;
    }

    public int getBurnDuration() {
        return burnDuration;
    }

    /** Restores the burn state from saved data. */
    public void load(int burnTime, int burnDuration) {
        this.burnTime = burnTime;
        this.burnDuration = burnDuration;
    }

    /**
     * Burns one tick of fuel if the buffer has room, starting the next fuel item when the last one burnt out.
     *
     * @param freeSpace     room left in the energy buffer
     * @param energyPerTick energy one burn tick produces
     * @return the energy produced this tick (at most {@code freeSpace}), or empty if the generator did not burn (it
     * is not lit then)
     */
    public OptionalLong burn(long freeSpace, long energyPerTick, FuelSlot fuel) {
        if (freeSpace <= 0) return OptionalLong.empty();
        if (burnTime <= 0) {
            int ticks = fuel.consumeNext();
            if (ticks <= 0) return OptionalLong.empty();
            burnTime = burnDuration = ticks;
        }
        burnTime--;
        return OptionalLong.of(Math.min(freeSpace, energyPerTick));
    }
}

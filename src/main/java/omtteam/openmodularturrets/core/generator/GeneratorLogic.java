package omtteam.openmodularturrets.core.generator;

import java.util.List;
import java.util.OptionalLong;

/**
 * Burning rules of the generator (new block, not in the 1.12.2 version): one item of fuel burns for its burn time,
 * and every burn tick produces the configured energy. Burning pauses while the energy buffer is full, so no fuel is
 * wasted. The next item comes from the first fuel slot that holds fuel, so the slots empty in order. The block entity
 * owns the fuel slots and the energy buffer and pushes the energy into its neighbours.
 */
public final class GeneratorLogic {
    /** Most energy multiplier upgrades that count (one upgrade slot holding 4). */
    public static final int MAX_MULTIPLIER_UPGRADES = 4;

    /** One of the generator's fuel slots. */
    @FunctionalInterface
    public interface FuelSlot {
        /**
         * Starts burning the next fuel item: removes it from the slot (leaving its remainder, like a furnace).
         *
         * @return its burn time in ticks, 0 if the slot holds no fuel (nothing is removed then)
         */
        int consumeNext();
    }

    /** Fuel ticks left of the item burning now (one fuel tick is one tick of burning without upgrades). */
    private int burnTime;
    /** Burn time in fuel ticks of the item burning now (for the flame in the GUI). */
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
     * Energy multiplier of {@code upgrades} energy multiplier upgrades: {@code 2^upgrades}, counting at most
     * {@link #MAX_MULTIPLIER_UPGRADES}. It multiplies both the energy produced per tick and the fuel burnt per tick, so
     * one fuel item still yields the same energy, just faster.
     */
    public static int multiplier(int upgrades) {
        return 1 << Math.max(0, Math.min(MAX_MULTIPLIER_UPGRADES, upgrades));
    }

    /**
     * Burns one tick if the buffer has room, starting the next fuel item when the last one burnt out. A tick burns
     * {@code multiplier} fuel ticks (fewer at the end of an item) and produces {@code energyPerTick} for each, so a
     * fuel item always yields {@code burnTime * energyPerTick} unless the buffer runs full.
     *
     * @param freeSpace     room left in the energy buffer
     * @param energyPerTick energy one fuel tick produces
     * @param multiplier    fuel ticks burnt per tick ({@link #multiplier}), at least 1
     * @param fuel          the fuel slots, in the order they are used
     * @return the energy produced this tick (at most {@code freeSpace}), or empty if the generator did not burn (it
     * is not lit then)
     */
    public OptionalLong burn(long freeSpace, long energyPerTick, int multiplier, List<? extends FuelSlot> fuel) {
        if (freeSpace <= 0) return OptionalLong.empty();
        if (burnTime <= 0) {
            int ticks = startNext(fuel);
            if (ticks <= 0) return OptionalLong.empty();
            burnTime = burnDuration = ticks;
        }
        int burnt = Math.min(Math.max(1, multiplier), burnTime);
        burnTime -= burnt;
        return OptionalLong.of(Math.min(freeSpace, burnt * energyPerTick));
    }

    /** Starts the next fuel item from the first slot that holds fuel; returns its burn time, 0 if none does. */
    private static int startNext(List<? extends FuelSlot> fuel) {
        for (FuelSlot slot : fuel) {
            int ticks = slot.consumeNext();
            if (ticks > 0) return ticks;
        }
        return 0;
    }
}

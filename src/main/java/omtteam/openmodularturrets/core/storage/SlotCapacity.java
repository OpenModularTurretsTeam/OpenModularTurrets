package omtteam.openmodularturrets.core.storage;

import java.util.Collection;
import java.util.function.IntUnaryOperator;

/**
 * How much one ammo slot of a turret base or fuel slot of the generator holds: one stack of the item, plus
 * {@code bonusStacks} more. The attached inventory expanders add their tier's {@code stacks} each; a turret base
 * multiplies the result by the stacks of its tier ({@code baseAmmoStacks}), so 64 * (1 + expanders) for tier 1
 * expanders, a one-stack base and a 64-stack item, twice that on a two-stack base. A record in 1.20.1+.
 */
public final class SlotCapacity {
    /** Most inventory expander plates that count on one face of a machine (one double expander block). */
    public static final int MAX_STACKED = 2;
    /** Highest inventory expander tier. */
    public static final int MAX_TIER = 3;
    /** One stack per slot (a generator or a one-stack base without expanders). */
    public static final SlotCapacity NONE = new SlotCapacity(0);

    private final int bonusStacks;

    /** @param bonusStacks stacks above the first; negative values count as 0 */
    public SlotCapacity(int bonusStacks) {
        this.bonusStacks = Math.max(0, bonusStacks);
    }

    /**
     * @param expanderTiers the tiers (1–{@value #MAX_TIER}) of all expanders attached to the machine
     * @param stacksPerTier stacks an expander of a tier adds ({@code LogicSettings::expanderStacks}); negative values
     *                      count as 0
     */
    public static SlotCapacity of(Collection<Integer> expanderTiers, IntUnaryOperator stacksPerTier) {
        long stacks = 0;
        for (int tier : expanderTiers) {
            stacks += Math.max(0, stacksPerTier.applyAsInt(tier));
        }
        return new SlotCapacity((int) Math.min(Integer.MAX_VALUE, stacks));
    }

    public int bonusStacks() {
        return bonusStacks;
    }

    /** Most items of one kind a slot may hold, for an item that stacks to {@code itemMaxStackSize}. */
    public int limit(int itemMaxStackSize) {
        return (int) Math.min(Integer.MAX_VALUE, (long) itemMaxStackSize * (1L + bonusStacks));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SlotCapacity && ((SlotCapacity) o).bonusStacks == bonusStacks;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bonusStacks);
    }

    @Override
    public String toString() {
        return "SlotCapacity[bonusStacks=" + bonusStacks + "]";
    }
}

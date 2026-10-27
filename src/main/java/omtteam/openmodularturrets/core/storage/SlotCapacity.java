package omtteam.openmodularturrets.core.storage;

import java.util.Collection;
import java.util.function.IntUnaryOperator;

/**
 * How much one ammo slot of a turret base or fuel slot of the generator holds: one stack of the item, plus
 * {@code bonusStacks} more from the attached inventory expanders, each adding its tier's {@code stacks}
 * (so 64 * (1 + expanders) for tier 1 expanders and a 64-stack item).
 */
public record SlotCapacity(int bonusStacks) {
    /** Most inventory expanders that count in a line from one face of a machine. */
    public static final int MAX_STACKED = 2;
    /** Highest inventory expander tier. */
    public static final int MAX_TIER = 3;
    /** A machine without expanders: one stack per slot. */
    public static final SlotCapacity NONE = new SlotCapacity(0);

    public SlotCapacity {
        bonusStacks = Math.max(0, bonusStacks);
    }

    /**
     * @param expanderTiers  the tiers (1–{@value #MAX_TIER}) of all expanders attached to the machine
     * @param stacksPerTier  stacks an expander of a tier adds ({@code LogicSettings::expanderStacks}); negative values
     *                       count as 0
     */
    public static SlotCapacity of(Collection<Integer> expanderTiers, IntUnaryOperator stacksPerTier) {
        long stacks = 0;
        for (int tier : expanderTiers) {
            stacks += Math.max(0, stacksPerTier.applyAsInt(tier));
        }
        return new SlotCapacity((int) Math.min(Integer.MAX_VALUE, stacks));
    }

    /** Most items of one kind a slot may hold, for an item that stacks to {@code itemMaxStackSize}. */
    public int limit(int itemMaxStackSize) {
        return (int) Math.min(Integer.MAX_VALUE, (long) itemMaxStackSize * (1L + bonusStacks));
    }
}

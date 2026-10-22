package omtteam.openmodularturrets.core.base;

/**
 * Slot layout per base tier (1.12.2 {@code TurretBase.setupSlotMap}).
 */
public final class BaseTier {
    public static final int MIN = 1;
    public static final int MAX = 5;
    public static final int AMMO_SLOTS = 9;

    private BaseTier() {
    }

    /** Two addon slots from tier 2. */
    public static int addonSlots(int tier) {
        return tier >= 2 ? 2 : 0;
    }

    /** One upgrade slot from tier 2, two on tier 5. */
    public static int upgradeSlots(int tier) {
        return tier >= 5 ? 2 : tier >= 2 ? 1 : 0;
    }

    /**
     * @throws IllegalArgumentException if {@code tier} is outside {@code [MIN, MAX]}
     */
    public static void validate(int tier) {
        if (tier < MIN || tier > MAX) throw new IllegalArgumentException("Invalid base tier: " + tier);
    }
}

package omtteam.openmodularturrets.core.camo;

/**
 * Light settings of a camouflaged block (1.12.2 OMLib {@code CamoSettings}): how much light it emits and how much
 * light it blocks, both 0–15 like vanilla. The disguise itself is a block state and lives in the game layer.
 *
 * @param value   light level the block emits
 * @param opacity light the block absorbs (15 = like a solid block, 0 = like glass)
 */
public record CamoLight(int value, int opacity) {
    public static final int MAX = 15;
    /** First turret base tier whose light can be changed (1.12.2 {@code ConfigureGui}: tier above 3). */
    public static final int MIN_BASE_TIER = 4;
    /** No light emitted, fully opaque: a camouflaged block behaves like a solid block unless changed. */
    public static final CamoLight DEFAULT = new CamoLight(0, MAX);

    public CamoLight {
        value = clamp(value);
        opacity = clamp(opacity);
    }

    /** {@code level} limited to {@code [0, MAX]}. */
    public static int clamp(int level) {
        return Math.max(0, Math.min(MAX, level));
    }

    /** Whether a turret base of this tier has adjustable light. */
    public static boolean supportedByBaseTier(int tier) {
        return tier >= MIN_BASE_TIER;
    }

    /** A copy with another emitted light level (clamped to {@code [0, MAX]}). */
    public CamoLight withValue(int newValue) {
        return new CamoLight(newValue, opacity);
    }

    /** A copy with another opacity (clamped to {@code [0, MAX]}). */
    public CamoLight withOpacity(int newOpacity) {
        return new CamoLight(value, newOpacity);
    }
}

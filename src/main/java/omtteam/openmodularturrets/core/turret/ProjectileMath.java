package omtteam.openmodularturrets.core.turret;

import omtteam.openmodularturrets.core.world.Vector3;

/**
 * Game rules of turret projectiles that do not depend on the game: damage amp bonus, damage splits of area
 * weapons, flight physics and ray scatter. The projectile entities only apply the results.
 */
public final class ProjectileMath {
    /** 1.12.2 {@code onUpdate}: projectiles live at most 40 ticks. */
    public static final int MAX_AGE = 40;
    private static final double DRAG_AIR = 0.99D;
    private static final double DRAG_WATER = 0.8D;
    /** Percent of accuracy deviation per unit of vanilla projectile inaccuracy. */
    private static final double INACCURACY_DIVISOR = 20D;

    private ProjectileMath() {
    }

    /** Damage split into a normal and an armor-piercing part. */
    public record Split(float normal, float armorBypass) {
    }

    /**
     * 1.12.2 {@code damage += (int) health * (damageAmp * ampLevel)}: the damage amp addon adds a share of the
     * target's current health (rounded down) to the damage.
     *
     * @param damageAmp the turret type's configured amp factor per addon level
     * @return {@code damage} unchanged if there is no amp or the target has no health
     */
    public static int damageWithAmp(int damage, int ampLevel, double damageAmp, float targetHealth) {
        if (ampLevel <= 0) return damage;
        return damage + (int) ((int) targetHealth * (damageAmp * ampLevel));
    }

    /** Grenades deal 90 % normal and 10 % armor-piercing damage. */
    public static Split grenadeSplit(int damage) {
        return new Split(damage * 0.9F, damage * 0.1F);
    }

    /** Plasma deals half normal and half armor-piercing damage. */
    public static Split plasmaSplit(int damage) {
        return new Split(damage * 0.5F, damage * 0.5F);
    }

    /**
     * Motion after one tick of flight: drag (stronger in water), then gravity unless disabled.
     *
     * @param dropPerTick downward acceleration of the projectile type
     */
    public static Vector3 nextMotion(Vector3 motion, boolean inWater, boolean noGravity, double dropPerTick) {
        Vector3 result = motion.scale(inWater ? DRAG_WATER : DRAG_AIR);
        return noGravity ? result : result.subtract(new Vector3(0, dropPerTick, 0));
    }

    /** Vanilla projectile inaccuracy for an accuracy deviation in percent. */
    public static float inaccuracy(double accuracyDeviation) {
        return (float) (accuracyDeviation / INACCURACY_DIVISOR);
    }

    /**
     * 1.12.2 {@code deviationModifier}: standard deviation (per axis, in blocks) of a ray's aim point. Small targets
     * (below half a block) and far targets scatter more.
     *
     * @param accuracyDeviation the turret's accuracy deviation in percent
     * @param distance          turret to aim point
     * @param range             the turret's range
     */
    public static double raySpread(double accuracyDeviation, double targetHeight, double distance, double range) {
        double deviationModifier = 10D * (targetHeight < 0.5 ? 1.5D : 1D) * (distance * 0.2D / range + 0.3D);
        return 0.003D * deviationModifier * accuracyDeviation;
    }
}

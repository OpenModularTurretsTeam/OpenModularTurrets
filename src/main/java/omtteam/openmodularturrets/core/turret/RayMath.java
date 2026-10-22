package omtteam.openmodularturrets.core.turret;

/**
 * Damage rules of the ray turrets (1.12.2 {@code LaserTurretTileEntity}, {@code RailGunTurretTileEntity},
 * {@code ArcTurretTileEntity} and {@code RayTracingTurret.damageEntity}). The ray weapons trace the rays and apply
 * the results.
 */
public final class RayMath {
    /** 1.12.2 {@code ArcTurretTileEntity}: {@code hop = 2} on launch, one jump per hit. */
    public static final int ARC_HOPS = 2;
    /**
     * 1.12.2 arc damage modifier {@code 1 / (3 - hop)}: the directly hit entity is damaged at hop 2 (full damage), the
     * jump target only after the chain reached hop 0 (a third).
     */
    public static final double ARC_JUMP_FACTOR = 1D / 3D;

    private RayMath() {
    }

    /** 1.12.2 {@code getDamageModifier(entity)} for the directly hit entity, see the {@link RayProfile} constants. */
    public static double damageModifier(RayProfile profile, int armor) {
        return profile.armorModifier().applyAsDouble(armor);
    }

    /**
     * 1.12.2 {@code RayTracingTurret.damageEntity}: base damage times the modifier, plus the damage amp bonus
     * ({@link ProjectileMath#ampBonus}, not rounded for rays).
     *
     * @param ampFactor    the base's amp factor for this turret (configured amp per level times the addon level)
     * @param targetHealth the target's current health, 0 for targets without health
     */
    public static float damage(int baseDamage, double modifier, double ampFactor, float targetHealth) {
        float damage = (float) (baseDamage * modifier);
        if (ampFactor > 0) {
            damage += (float) ProjectileMath.ampBonus(targetHealth, ampFactor);
        }
        return damage;
    }

    /**
     * How a ray's damage is dealt ({@link RayProfile#normalShare()} / {@link RayProfile#bypassShare()}): the laser
     * only normal damage, the railgun only armor-piercing damage, the arc 60 % normal and 40 % armor-piercing.
     */
    public static ProjectileMath.Split split(RayProfile profile, float damage) {
        return new ProjectileMath.Split(damage * profile.normalShare(), damage * profile.bypassShare());
    }

    /** 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: the arc jumps up to {@code 3 * remaining hops} blocks. */
    public static double arcJumpRange(int hops) {
        return 3D * hops;
    }
}

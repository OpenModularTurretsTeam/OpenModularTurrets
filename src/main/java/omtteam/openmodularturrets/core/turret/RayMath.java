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

    /**
     * 1.12.2 {@code getDamageModifier(entity)} for the directly hit entity:
     * <ul>
     *     <li>laser: {@code (30 - armor) / 20 + 0.1}, so 1.6x without armor down to 0.6x at 20 armor</li>
     *     <li>railgun: {@code armor / 20 + 0.6}, so 0.6x without armor up to 1.6x at 20 armor</li>
     *     <li>arc: always 1</li>
     * </ul>
     */
    public static double damageModifier(TurretType type, int armor) {
        return switch (type) {
            case LASER_TURRET -> (30 - armor) / 20F + 0.1F;
            case RAILGUN_TURRET -> armor / 20F + 0.6F;
            case ARC_TURRET -> 1F;
            default -> throw new IllegalArgumentException(type + " is not a ray turret");
        };
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
     * How a ray's damage is dealt: the laser only normal damage, the railgun only armor-piercing damage, the arc 60 %
     * normal and 40 % armor-piercing.
     */
    public static ProjectileMath.Split split(TurretType type, float damage) {
        return switch (type) {
            case LASER_TURRET -> new ProjectileMath.Split(damage, 0F);
            case RAILGUN_TURRET -> new ProjectileMath.Split(0F, damage);
            case ARC_TURRET -> new ProjectileMath.Split(damage * 0.6F, damage * 0.4F);
            default -> throw new IllegalArgumentException(type + " is not a ray turret");
        };
    }

    /** 1.12.2 {@code ArcTurretTileEntity.applyHitEffects}: the arc jumps up to {@code 3 * remaining hops} blocks. */
    public static double arcJumpRange(int hops) {
        return 3D * hops;
    }
}

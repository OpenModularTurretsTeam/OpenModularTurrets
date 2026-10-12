package omtteam.openmodularturrets.core.turret;

import java.util.Optional;

/**
 * All turret types with the minimum base tier they need and the ammunition they consume. Ported from the oldmod turret
 * tile entities ({@code super(tier)}, {@code ammo = …}, {@code requiresAmmo()}).
 * <p>
 * Numeric stats (range, fire rate, power, upgrades) are configurable and come from
 * {@link omtteam.openmodularturrets.core.settings.LogicSettings}.
 */
public enum TurretType {
    DISPOSABLE_TURRET("disposable_turret", 1, AmmoType.DISPOSABLE, new int[]{5, 5, -5, 1, 10}),
    POTATO_CANNON_TURRET("potato_cannon_turret", 1, AmmoType.POTATO, new int[]{-5, -5, -5, 1, 10}),
    CROSSBOW_TURRET("crossbow_turret", 1, AmmoType.ARROW, new int[]{-5, -5, -5, 1, 10}),
    MELEE_TURRET("melee_turret", 1, null, new int[]{1, 1, 0, 10, 10}),
    MACHINE_GUN_TURRET("machine_gun_turret", 2, AmmoType.BULLET, new int[]{1, 10, -5, -5, 10}),
    INCENDIARY_TURRET("incendiary_turret", 2, AmmoType.BLAZING_CLAY, new int[]{10, -5, -2, 10, 10}),
    GRENADE_TURRET("grenade_turret", 3, AmmoType.GRENADE, new int[]{10, 10, 20, 1, 10}),
    RELATIVISTIC_TURRET("relativistic_turret", 3, null, new int[]{1, 1, 0, 10, 10}),
    ROCKET_TURRET("rocket_turret", 4, AmmoType.ROCKET, new int[]{10, 10, 20, 1, 10}),
    TELEPORTER_TURRET("teleporter_turret", 4, null, new int[]{1, -10, 0, 10, 10}),
    ARC_TURRET("arc_turret", 4, null, new int[]{5, 10, 2, -10, 10}),
    LASER_TURRET("laser_turret", 5, null, new int[]{5, 10, 2, -10, 10}),
    RAILGUN_TURRET("railgun_turret", 5, AmmoType.FERRO_SLUG, new int[]{10, 10, 20, 40, 10}),
    PLASMA_TURRET("plasma_turret", 5, null, new int[]{10, 10, 20, 1, 10});

    private final String id;
    private final int requiredBaseTier;
    private final AmmoType ammo;
    private final int[] priorities;

    TurretType(String id, int requiredBaseTier, AmmoType ammo, int[] priorities) {
        this.id = id;
        this.requiredBaseTier = requiredBaseTier;
        this.ammo = ammo;
        this.priorities = priorities;
    }

    /** Registry path, also the key of the turret's config section. */
    public String getId() {
        return id;
    }

    /** A turret on a base of a lower tier breaks (oldmod {@code base.getTier() < turretTier}). */
    public int getRequiredBaseTier() {
        return requiredBaseTier;
    }

    /** The ammunition this turret fires; empty for energy-only turrets. Ignores {@code doTurretsNeedAmmo}. */
    public Optional<AmmoType> getAmmo() {
        return Optional.ofNullable(ammo);
    }

    /**
     * Default targeting weights in {@link omtteam.openmodularturrets.core.target.TargetingPriority} order (oldmod
     * {@code getDefaultPriorities}). Melee, relativistic and teleporter had only four values in oldmod (a crash
     * bug); their PLAYER weight is 10 like every other turret (user decision).
     */
    public int[] getDefaultPriorities() {
        return priorities.clone();
    }

    public static Optional<TurretType> byId(String id) {
        for (TurretType type : values()) {
            if (type.id.equals(id)) return Optional.of(type);
        }
        return Optional.empty();
    }
}

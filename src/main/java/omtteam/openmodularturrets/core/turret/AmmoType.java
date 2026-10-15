package omtteam.openmodularturrets.core.turret;

/**
 * Item ammunition used by turrets. Ported from 1.12.2 {@code AmmoMetaItem} (meta 0–5) plus the vanilla items some
 * turrets fire. Energy-only turrets have no ammo type.
 */
public enum AmmoType {
    /** 1.12.2 {@code AmmoMetaItem} meta 0. */
    BLAZING_CLAY("blazing_clay"),
    /** 1.12.2 {@code AmmoMetaItem} meta 1. */
    BULLET("bullet"),
    /** 1.12.2 {@code AmmoMetaItem} meta 2. */
    FERRO_SLUG("ferro_slug"),
    /** 1.12.2 {@code AmmoMetaItem} meta 3. */
    GRENADE("grenade"),
    /** 1.12.2 {@code AmmoMetaItem} meta 4. */
    ROCKET("rocket"),
    /** Vanilla potato. */
    POTATO("potato"),
    /** New in 4.0: crossbow bolt, ammo of the crossbow turret. */
    BOLT("bolt"),
    /**
     * Any item on the config's ammo allow list (1.12.2 "fake disposable item", meta 5). The item's damage bonus is
     * reported with each draw.
     */
    DISPOSABLE("disposable");

    private final String serializedName;

    AmmoType(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return serializedName;
    }
}

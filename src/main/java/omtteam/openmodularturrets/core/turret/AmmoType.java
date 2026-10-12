package omtteam.openmodularturrets.core.turret;

/**
 * Item ammunition used by turrets. Ported from oldmod {@code AmmoMetaItem} (meta 0–5) plus the vanilla items some
 * turrets fire. Energy-only turrets have no ammo type.
 */
public enum AmmoType {
    /** oldmod {@code AmmoMetaItem} meta 0. */
    BLAZING_CLAY("blazing_clay"),
    /** oldmod {@code AmmoMetaItem} meta 1. */
    BULLET("bullet"),
    /** oldmod {@code AmmoMetaItem} meta 2. */
    FERRO_SLUG("ferro_slug"),
    /** oldmod {@code AmmoMetaItem} meta 3. */
    GRENADE("grenade"),
    /** oldmod {@code AmmoMetaItem} meta 4. */
    ROCKET("rocket"),
    /** Vanilla potato. */
    POTATO("potato"),
    /** Vanilla (untipped) arrow. */
    ARROW("arrow"),
    /**
     * Any item on the config's ammo allow list (oldmod "fake disposable item", meta 5). The item's damage bonus is
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

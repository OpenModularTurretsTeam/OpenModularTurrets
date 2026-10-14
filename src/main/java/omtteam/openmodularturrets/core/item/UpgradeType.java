package omtteam.openmodularturrets.core.item;

/**
 * Upgrades that go into the upgrade slots of a base. Ported from 1.12.2 {@code UpgradeMetaItem} (meta 0–4).
 */
public enum UpgradeType {
    ACCURACY("accuracy"),
    EFFICIENCY("efficiency"),
    FIRE_RATE("fire_rate"),
    RANGE("range"),
    SCATTERSHOT("scatter_shot"),
    /** New in 4.0: fills the energy storage and removes the ammo usage of the base's turrets. */
    CREATIVE("creative");

    /** 1.12.2 {@code UpgradeSlot.getSlotStackLimit}: "Stacks up to 4 times". */
    public static final int MAX_PER_SLOT = 4;

    private final String serializedName;

    UpgradeType(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return serializedName;
    }
}

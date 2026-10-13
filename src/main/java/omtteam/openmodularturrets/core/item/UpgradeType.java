package omtteam.openmodularturrets.core.item;

/**
 * Upgrades that go into the upgrade slots of a base. Ported from 1.12.2 {@code UpgradeMetaItem} (meta 0–4).
 */
public enum UpgradeType {
    ACCURACY("accuracy"),
    EFFICIENCY("efficiency"),
    FIRE_RATE("fire_rate"),
    RANGE("range"),
    SCATTERSHOT("scatter_shot");

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

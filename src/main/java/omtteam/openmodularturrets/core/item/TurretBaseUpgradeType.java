package omtteam.openmodularturrets.core.item;

/**
 * Upgrades that go into the upgrade slots of a turret base. Ported from 1.12.2 {@code UpgradeMetaItem} (meta 0–4).
 */
public enum TurretBaseUpgradeType implements UpgradeType {
    ACCURACY("accuracy"),
    EFFICIENCY("efficiency"),
    FIRE_RATE("fire_rate"),
    RANGE("range"),
    SCATTERSHOT("scatter_shot"),
    /** New in 4.0: fills the energy storage and removes the ammo usage of the base's turrets. */
    CREATIVE("creative");

    private final String serializedName;

    TurretBaseUpgradeType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}

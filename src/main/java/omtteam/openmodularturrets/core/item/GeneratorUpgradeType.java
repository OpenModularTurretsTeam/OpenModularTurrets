package omtteam.openmodularturrets.core.item;

/**
 * Upgrades for the upgrade slot of the generator.
 */
public enum GeneratorUpgradeType implements UpgradeType {
    /** Doubles energy production and fuel usage per upgrade (see {@code GeneratorLogic#multiplier}). */
    ENERGY_MULTIPLIER("energy_multiplier");

    private final String serializedName;

    GeneratorUpgradeType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}

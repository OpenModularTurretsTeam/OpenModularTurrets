package omtteam.openmodularturrets.core.item;

/**
 * An upgrade for one kind of machine. Each machine has its own upgrade list ({@link TurretBaseUpgradeType},
 * {@link GeneratorUpgradeType}); its upgrade slots only accept upgrades of that list.
 */
public interface UpgradeType {
    /** The id part after {@code upgrade_}, also used in translation keys. */
    String getSerializedName();

    /** How many of this upgrade one upgrade slot holds (1.12.2 {@code UpgradeSlot}: "Stacks up to 4 times"). */
    default int maxPerSlot() {
        return 4;
    }
}

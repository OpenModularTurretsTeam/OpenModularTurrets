package omtteam.openmodularturrets.item;

import omtteam.openmodularturrets.core.item.GeneratorUpgradeType;

/**
 * Upgrade for the upgrade slot of the generator.
 */
public class GeneratorUpgradeItem extends UpgradeItem {
    private final GeneratorUpgradeType upgradeType;

    public GeneratorUpgradeItem(GeneratorUpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    @Override
    public GeneratorUpgradeType getUpgradeType() {
        return upgradeType;
    }
}

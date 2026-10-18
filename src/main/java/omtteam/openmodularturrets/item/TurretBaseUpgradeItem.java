package omtteam.openmodularturrets.item;

import omtteam.openmodularturrets.core.item.TurretBaseUpgradeType;

/**
 * Upgrade for the upgrade slots of a turret base. Ported from 1.12.2 {@code UpgradeMetaItem} (one item per meta).
 */
public class TurretBaseUpgradeItem extends UpgradeItem {
    private final TurretBaseUpgradeType upgradeType;

    public TurretBaseUpgradeItem(TurretBaseUpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    @Override
    public TurretBaseUpgradeType getUpgradeType() {
        return upgradeType;
    }
}

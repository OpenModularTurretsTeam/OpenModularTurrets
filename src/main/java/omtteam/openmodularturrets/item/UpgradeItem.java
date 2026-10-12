package omtteam.openmodularturrets.item;

import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.core.item.UpgradeType;

/**
 * Turret upgrade for the upgrade slots of a base. Ported from oldmod {@code UpgradeMetaItem} (one item per meta).
 */
public class UpgradeItem extends Item {
    private final UpgradeType upgradeType;

    public UpgradeItem(UpgradeType upgradeType, Properties properties) {
        super(properties);
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }
}

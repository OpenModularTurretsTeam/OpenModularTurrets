package omtteam.openmodularturrets.item;

import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.core.item.UpgradeType;

/**
 * An upgrade item. Each machine has its own subclass and upgrade list ({@link TurretBaseUpgradeItem},
 * {@link GeneratorUpgradeItem}); a machine's upgrade slots only accept its own subclass.
 */
public abstract class UpgradeItem extends Item {
    protected UpgradeItem(Properties properties) {
        super(properties);
    }

    public abstract UpgradeType getUpgradeType();
}

package omtteam.openmodularturrets.item;

import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.core.item.AddonType;

/**
 * Turret addon for the addon slots of a base. Ported from 1.12.2 {@code AddonMetaItem} (one item per meta).
 */
public class AddonItem extends Item {
    private final AddonType addonType;

    public AddonItem(AddonType addonType, Properties properties) {
        super(properties);
        this.addonType = addonType;
    }

    public AddonType getAddonType() {
        return addonType;
    }
}

package omtteam.openmodularturrets.item;

import net.minecraft.world.item.Item;
import omtteam.openmodularturrets.core.turret.AmmoType;

/**
 * Turret ammunition crafted from vanilla materials. Ported from 1.12.2 {@code AmmoMetaItem} (meta 0–4, flattened into
 * one item per type). The internal "fake disposable item" (meta 5) is not needed and not registered.
 */
public class AmmoItem extends Item {
    private final AmmoType ammoType;

    public AmmoItem(AmmoType ammoType, Properties properties) {
        super(properties);
        this.ammoType = ammoType;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }
}

package openmodularturrets.items.ammo;

import openmodularturrets.ModularTurrets;
import net.minecraft.item.Item;

public abstract class AmmoItem extends Item {
    public AmmoItem() {
        super();

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}

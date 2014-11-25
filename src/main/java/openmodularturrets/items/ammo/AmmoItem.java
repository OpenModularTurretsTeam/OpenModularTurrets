package openmodularturrets.items.ammo;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;

public abstract class AmmoItem extends Item {
    public AmmoItem() {
        super();

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}

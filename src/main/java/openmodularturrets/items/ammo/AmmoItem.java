package openmodularturrets.items.ammo;

import net.minecraft.item.Item;

import openmodularturrets.ModularTurrets;

abstract class AmmoItem extends Item {

    AmmoItem() {
        super();
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}

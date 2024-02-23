package openmodularturrets.items.upgrades;

import net.minecraft.item.Item;

import openmodularturrets.ModularTurrets;

public abstract class UpgradeItem extends Item {

    UpgradeItem() {
        super();
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setMaxStackSize(4);
    }
}

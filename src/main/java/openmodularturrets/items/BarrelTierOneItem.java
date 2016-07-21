package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class BarrelTierOneItem extends Item {
    public BarrelTierOneItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedBarrelTierOne);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
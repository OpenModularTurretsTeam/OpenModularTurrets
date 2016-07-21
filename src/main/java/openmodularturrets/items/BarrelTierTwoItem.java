package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class BarrelTierTwoItem extends Item {
    public BarrelTierTwoItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedBarrelTierTwo);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class SensorTierThreeItem extends Item {
    public SensorTierThreeItem() {
        super();
        this.setUnlocalizedName(Names.Items.unlocalisedSensorTierThree);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
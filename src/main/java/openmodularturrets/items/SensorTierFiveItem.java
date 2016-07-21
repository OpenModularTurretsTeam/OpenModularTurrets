package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class SensorTierFiveItem extends Item {
    public SensorTierFiveItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedSensorTierFive);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
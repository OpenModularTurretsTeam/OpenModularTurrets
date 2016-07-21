package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class SensorTierFourItem extends Item {
    public SensorTierFourItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedSensorTierFour);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
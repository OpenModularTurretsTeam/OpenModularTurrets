package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class SensorTierTwoItem extends Item {
    public SensorTierTwoItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedSensorTierTwo);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
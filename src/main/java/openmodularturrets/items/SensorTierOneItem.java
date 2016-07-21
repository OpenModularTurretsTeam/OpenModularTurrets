package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class SensorTierOneItem extends Item {
    public SensorTierOneItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedSensorTierOne);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
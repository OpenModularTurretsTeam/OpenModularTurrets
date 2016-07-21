package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class ChamberTierOneItem extends Item {
    public ChamberTierOneItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedChamberTierOne);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
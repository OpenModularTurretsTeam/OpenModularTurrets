package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class ChamberTierTwoItem extends Item {
    public ChamberTierTwoItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedChamberTierTwo);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
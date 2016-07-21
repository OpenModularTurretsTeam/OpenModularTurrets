package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class IOBusItem extends Item {
    public IOBusItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedIOBus);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
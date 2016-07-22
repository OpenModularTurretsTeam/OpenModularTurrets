package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

public class IOBusItem extends Item {
    public IOBusItem() {
        super();

        this.setUnlocalizedName(Names.Items.ioBus);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
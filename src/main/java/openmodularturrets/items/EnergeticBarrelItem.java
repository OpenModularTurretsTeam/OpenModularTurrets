package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

class EnergeticBarrelItem extends Item {
    public EnergeticBarrelItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedEnergeticBarrel);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
package openmodularturrets.items;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

public class EnergeticBarrelItem extends Item {
    public EnergeticBarrelItem() {
        super();

        this.setUnlocalizedName(Names.Items.energeticBarrel);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
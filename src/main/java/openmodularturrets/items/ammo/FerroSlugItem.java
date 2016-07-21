package openmodularturrets.items.ammo;

import net.minecraft.item.Item;
import openmodularturrets.ModularTurrets;
import openmodularturrets.reference.Names;

public class FerroSlugItem extends Item {
    public FerroSlugItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedFerroSlug);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }
}
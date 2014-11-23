package modularTurrets.items.addons;

import modularTurrets.ModularTurrets;
import net.minecraft.item.Item;

public abstract class AddonItem extends Item {

	public AddonItem() {
		super();

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setMaxStackSize(1);
	}
}

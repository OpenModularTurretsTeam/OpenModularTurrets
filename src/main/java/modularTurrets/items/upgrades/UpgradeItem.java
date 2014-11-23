package modularTurrets.items.upgrades;

import modularTurrets.ModularTurrets;
import net.minecraft.item.Item;

public abstract class UpgradeItem extends Item
{
	public UpgradeItem() {
		super();

        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setMaxStackSize(4);
	}
}

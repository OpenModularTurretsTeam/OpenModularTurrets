package modularTurrets.misc;

import modularTurrets.blocks.BlockIDs;
import net.minecraft.creativetab.CreativeTabs;

public class ModularTurretsTab extends CreativeTabs {

	public ModularTurretsTab(int par1, String par2Str) {
		super(par1, par2Str);
	}

	@Override
	public int getTabIconItemIndex() {

		return BlockIDs.machineGunTurret;
	}

}

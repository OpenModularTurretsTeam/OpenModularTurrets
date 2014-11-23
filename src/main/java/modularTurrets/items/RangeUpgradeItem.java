package modularTurrets.items;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import net.minecraft.client.renderer.texture.IconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RangeUpgradeItem extends UpgradeItem {

	public RangeUpgradeItem(int par1) {
		super(par1);
		this.setUnlocalizedName(ItemNames.unlocalisedRangeUpgrade);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setMaxStackSize(4);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":rangeUpgrade");
	}

}
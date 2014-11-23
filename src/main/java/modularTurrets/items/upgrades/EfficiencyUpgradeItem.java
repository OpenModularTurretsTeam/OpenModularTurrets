package modularTurrets.items.upgrades;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.ModInfo;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;

public class EfficiencyUpgradeItem extends UpgradeItem {

	public EfficiencyUpgradeItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedEfficiencyUpgrade);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":efficiencyUpgrade");
	}
}
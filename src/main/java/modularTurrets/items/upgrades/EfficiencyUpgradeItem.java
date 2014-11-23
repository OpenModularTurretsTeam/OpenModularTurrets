package modularTurrets.items.upgrades;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EfficiencyUpgradeItem extends UpgradeItem {

	public EfficiencyUpgradeItem() {
		super();
		this.setUnlocalizedName(ItemNames.unlocalisedEfficiencyUpgrade);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setMaxStackSize(4);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":efficiencyUpgrade");
	}

}
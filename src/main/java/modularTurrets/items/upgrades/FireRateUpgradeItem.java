package modularTurrets.items.upgrades;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.ModInfo;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;

public class FireRateUpgradeItem extends UpgradeItem {

	public FireRateUpgradeItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedFireRateUpgrade);
    }

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":fireRateUpgrade");
	}
}
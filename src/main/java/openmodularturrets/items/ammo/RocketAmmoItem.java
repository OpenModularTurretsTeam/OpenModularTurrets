package openmodularturrets.items.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import openmodularturrets.ModInfo;
import openmodularturrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;

public class RocketAmmoItem extends AmmoItem {

	public RocketAmmoItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedRocketCraftableItem);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":rocketAmmo");
	}
}
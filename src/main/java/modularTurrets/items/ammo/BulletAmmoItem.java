package modularTurrets.items.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.ModInfo;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BulletAmmoItem extends AmmoItem {

	public BulletAmmoItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedBulletCraftableItem);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":bulletAmmo");
	}
}
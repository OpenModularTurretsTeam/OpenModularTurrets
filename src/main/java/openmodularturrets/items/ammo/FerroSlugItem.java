package openmodularturrets.items.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import openmodularturrets.items.ItemNames;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.ModularTurrets;

public class FerroSlugItem extends Item {

	public FerroSlugItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedFerroSlug);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":ferroSlug");
	}
}
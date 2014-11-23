package modularTurrets.items;

import modularTurrets.ModInfo;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GrenadeThrowableItem extends Item {

	public GrenadeThrowableItem(int par1) {
		super(par1);
		this.setUnlocalizedName(ItemNames.unlocalisedGrenadeThrowableItem);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":grenade");
	}

}
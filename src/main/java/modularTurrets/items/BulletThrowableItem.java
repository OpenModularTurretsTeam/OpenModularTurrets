package modularTurrets.items;

import modularTurrets.ModInfo;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BulletThrowableItem extends Item {

	public BulletThrowableItem(int par1) {
		super(par1);
		this.setUnlocalizedName(ItemNames.unlocalisedBulletThrowableItem);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":bullet");
	}

}
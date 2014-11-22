package modularTurrets.items;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import net.minecraft.client.renderer.texture.IconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DamageAmpAddonItem extends AddonItem {

	public DamageAmpAddonItem(int par1) {
		super(par1);
		this.setUnlocalizedName(ItemNames.unlocalisedDamageAmpAddon);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":damageAmpAddon");
	}

}
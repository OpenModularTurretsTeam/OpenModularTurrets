package modularTurrets.items.addons;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DamageAmpAddonItem extends AddonItem {

	public DamageAmpAddonItem() {
		super();
		this.setUnlocalizedName(ItemNames.unlocalisedDamageAmpAddon);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setMaxStackSize(1);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":damageAmpAddon");
	}

}
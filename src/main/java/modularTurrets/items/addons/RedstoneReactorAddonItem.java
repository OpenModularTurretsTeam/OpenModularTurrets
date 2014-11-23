package modularTurrets.items.addons;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.items.ItemNames;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedstoneReactorAddonItem extends AddonItem {

	public RedstoneReactorAddonItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedRedReactorAddon);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":ammoProductionAddon");
	}
}
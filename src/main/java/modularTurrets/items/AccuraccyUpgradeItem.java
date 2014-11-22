package modularTurrets.items;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AccuraccyUpgradeItem extends UpgradeItem {

	public AccuraccyUpgradeItem() {
		super();
		this.setUnlocalizedName(ItemNames.unlocalisedAccuraccyUpgrade);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
		this.setMaxStackSize(4);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":accuraccyUpgrade");
	}
}
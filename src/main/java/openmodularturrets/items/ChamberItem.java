package openmodularturrets.items;

import openmodularturrets.ModInfo;
import openmodularturrets.ModularTurrets;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChamberItem extends Item {

	public ChamberItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedChamber);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":chamber");
	}
}
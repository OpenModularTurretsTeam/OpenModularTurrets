package openmodularturrets.items;

import openmodularturrets.ModInfo;
import openmodularturrets.ModularTurrets;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainmentChamberItem extends Item {

	public ContainmentChamberItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedContainmentChamber);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":containmentChamber");
	}
}
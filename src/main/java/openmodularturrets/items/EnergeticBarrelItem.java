package openmodularturrets.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import openmodularturrets.ModInfo;
import openmodularturrets.ModularTurrets;

public class EnergeticBarrelItem extends Item {

	public EnergeticBarrelItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedEnergeticBarrel);
		this.setCreativeTab(ModularTurrets.modularTurretsTab);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":energeticBarrel");
	}
}
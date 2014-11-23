package modularTurrets.items.addons;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.items.ItemNames;
import modularTurrets.misc.Constants;
import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class DamageAmpAddonItem extends AddonItem {

	public DamageAmpAddonItem() {
		super();

		this.setUnlocalizedName(ItemNames.unlocalisedDamageAmpAddon);
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":damageAmpAddon");
	}

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A74Turret Addon");
        p_77624_3_.add("");
        p_77624_3_.add("Adds "+ Constants.damageAmpDmgBonus+" damage per projectile.");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78Here we are juggernaught.");
    }
}
package modularTurrets.items;

import modularTurrets.ModInfo;
import modularTurrets.ModularTurrets;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConfigTabItem extends Item {

    public ConfigTabItem() {
	super();
	this.setUnlocalizedName(ItemNames.unlocalisedConfigTablet);
	this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
	this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase()
		+ ":config");
    }

    @Override
    public boolean onItemUse(ItemStack par1ItemStack,
	    EntityPlayer par2EntityPlayer, World par3World, int par4, int par5,
	    int par6, int par7, float par8, float par9, float par10) {

	if (par2EntityPlayer.isSneaking() && !par3World.isRemote) {
	    if (par3World.getTileEntity(par4, par5, par6) instanceof TurretBase) {
		TurretBase base = (TurretBase) par3World.getTileEntity(
			par4, par5, par6);
		if (par2EntityPlayer.getDisplayName().equals(base.getOwner())) {
		    par2EntityPlayer.openGui(ModularTurrets.instance, 5,
			    par3World, par4, par5, par6);
		} else {
		    if (!par3World.isRemote) {
		    par2EntityPlayer
			    .addChatMessage(new ChatComponentText("You do not own this turret."));
		    }
		}
	    } else {
		 if (!par3World.isRemote) {
		par2EntityPlayer
			.addChatMessage(new ChatComponentText("There's no turret base there."));
		 }
	    }
	}

	return true;
    }
}
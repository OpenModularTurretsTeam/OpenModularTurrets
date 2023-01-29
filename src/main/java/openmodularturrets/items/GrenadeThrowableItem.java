package openmodularturrets.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

class GrenadeThrowableItem extends Item {

    public GrenadeThrowableItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedGrenadeThrowableItem);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":grenade");
    }
}

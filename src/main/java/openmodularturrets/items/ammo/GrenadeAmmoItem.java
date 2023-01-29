package openmodularturrets.items.ammo;

import net.minecraft.client.renderer.texture.IIconRegister;

import openmodularturrets.reference.ModInfo;
import openmodularturrets.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GrenadeAmmoItem extends AmmoItem {

    public GrenadeAmmoItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedGrenadeCraftableItem);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":grenadeAmmo");
    }
}

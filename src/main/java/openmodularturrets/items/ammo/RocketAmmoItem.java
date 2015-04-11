package openmodularturrets.items.ammo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import openmodularturrets.items.ItemNames;
import openmodularturrets.reference.ModInfo;

public class RocketAmmoItem extends AmmoItem {

    public RocketAmmoItem() {
        super();

        this.setUnlocalizedName(ItemNames.unlocalisedRocketCraftableItem);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":rocketAmmo");
    }
}
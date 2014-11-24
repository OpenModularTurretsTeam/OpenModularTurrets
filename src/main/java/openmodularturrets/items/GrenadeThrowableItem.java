package openmodularturrets.items;
import openmodularturrets.ModInfo;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GrenadeThrowableItem extends Item {
    public GrenadeThrowableItem() {
        super();

        this.setUnlocalizedName(ItemNames.unlocalisedGrenadeThrowableItem);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":grenade");
    }
}
package openmodularturrets.items.upgrades;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.ItemNames;
import openmodularturrets.reference.ModInfo;

import java.util.List;

public class FireRateUpgradeItem extends UpgradeItem {

    public FireRateUpgradeItem() {
        super();

        this.setUnlocalizedName(ItemNames.unlocalisedFireRateUpgrade);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(ModInfo.ID.toLowerCase() + ":fireRateUpgrade");
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
        p_77624_3_.add("");
        p_77624_3_.add(
                "+ " + ConfigHandler.getFireRateUpgradeBoostPercentage() * 100 + "% " + StatCollector.translateToLocal(
                        "turret.upgrade.rof"));
        p_77624_3_.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.rof.flavour"));
    }
}
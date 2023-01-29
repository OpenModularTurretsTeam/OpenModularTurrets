package openmodularturrets.items.blocks;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import openmodularturrets.handler.ConfigHandler;

public class ItemBlockRocketTurret extends ItemBlock {

    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockRocketTurret(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.GOLD + "--" + StatCollector.translateToLocal("tooltip.info") + "--");
        p_77624_3_.add(StatCollector.translateToLocal("tooltip.tier") + ": " + EnumChatFormatting.WHITE + "4");
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.range") + ": "
                        + EnumChatFormatting.WHITE
                        + ConfigHandler.getRocketTurretSettings().getRange());
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.accuracy") + ": "
                        + EnumChatFormatting.WHITE
                        + StatCollector.translateToLocal("turret.accuracy.exact"));
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.ammo") + ": "
                        + EnumChatFormatting.WHITE
                        + StatCollector.translateToLocal("turret.ammo.3"));
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.tierRequired") + ": "
                        + EnumChatFormatting.WHITE
                        + StatCollector.translateToLocal("base.tier.4"));
        p_77624_3_.add("");
        p_77624_3_.add(
                EnumChatFormatting.DARK_PURPLE + "--" + StatCollector.translateToLocal("tooltip.damage.label") + "--");
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.damage.stat") + ": "
                        + EnumChatFormatting.WHITE
                        + (ConfigHandler.getRocketTurretSettings().getDamage() / 2F)
                        + " "
                        + StatCollector.translateToLocal("tooltip.health"));
        p_77624_3_.add(StatCollector.translateToLocal("tooltip.aoe") + ": " + EnumChatFormatting.WHITE + "5");
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.firerate") + ": "
                        + EnumChatFormatting.WHITE
                        + df.format(20.0F / ConfigHandler.getRocketTurretSettings().getFireRate()));
        p_77624_3_.add(
                StatCollector.translateToLocal("tooltip.energy.stat") + ": "
                        + EnumChatFormatting.WHITE
                        + ConfigHandler.getRocketTurretSettings().getPowerUsage()
                        + " RF");
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.turret.3"));
    }
}

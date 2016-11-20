package openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import openmodularturrets.ModularTurrets;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModItems;
import openmodularturrets.reference.Names;

import java.util.List;

public class UpgradeMetaItem extends Item {
    public UpgradeMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
        this.setMaxStackSize(4);
    }

    public final static String[] subNames = {
            Names.Items.accuraccyUpgrade, Names.Items.efficiencyUpgrade, Names.Items.fireRateUpgrade,
            Names.Items.rangeUpgrade, Names.Items.scattershotUpgrade
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModItems.upgradeMetaItem, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add("+ " + ConfigHandler.getAccuracyUpgradeBoost() * 100 + "% " + StatCollector.translateToLocal(
                        "turret.upgrade.acc"));
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.acc.flavour.a"));
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.acc.flavour.b"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(
                        "- " + ConfigHandler.getEfficiencyUpgradeBoostPercentage() * 100 + "% " + StatCollector.translateToLocal(
                                "turret.upgrade.eff"));
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.eff.flavour"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(
                        "+ " + ConfigHandler.getFireRateUpgradeBoostPercentage() * 100 + "% " + StatCollector.translateToLocal(
                                "turret.upgrade.rof"));
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.rof.flavour"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add("+ " + ConfigHandler.getRangeUpgradeBoost() + " " + StatCollector.translateToLocal(
                        "turret" + ".upgrade.range"));
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.range.flavour"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.scatter.a"));
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.scatter.b"));
                tooltip.add("");
                tooltip.add(StatCollector.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.upgrade.scatter.flavour"));
        }
    }
}

package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import omtteam.omlib.compatability.minecraft.CompatItem;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings("deprecation")
public class UpgradeMetaItem extends CompatItem {
    public UpgradeMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.upgradeMetaItem);
        this.setUnlocalizedName(OMTNames.Items.upgradeMetaItem);
    }

    public final static String[] subNames = {
            OMTNames.Items.accuraccyUpgrade, OMTNames.Items.efficiencyUpgrade, OMTNames.Items.fireRateUpgrade,
            OMTNames.Items.rangeUpgrade, OMTNames.Items.scattershotUpgrade
    };

    @Override
    @ParametersAreNonnullByDefault
    public void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModItems.upgradeMetaItem, 1, i));
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add("+ " + ConfigHandler.getAccuracyUpgradeBoost() * 100 + "% " + I18n.translateToLocal(
                        "turret.upgrade.acc"));
                tooltip.add(I18n.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.acc.flavour.a"));
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.acc.flavour.b"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(
                        "- " + ConfigHandler.getEfficiencyUpgradeBoostPercentage() * 100 + "% " + I18n.translateToLocal(
                                "turret.upgrade.eff"));
                tooltip.add(I18n.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.eff.flavour"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(
                        "+ " + ConfigHandler.getFireRateUpgradeBoostPercentage() * 100 + "% " + I18n.translateToLocal(
                                "turret.upgrade.rof"));
                tooltip.add(I18n.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.rof.flavour"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add("+ " + ConfigHandler.getRangeUpgradeBoost() + " " + I18n.translateToLocal(
                        "turret" + ".upgrade.range"));
                tooltip.add(I18n.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.range.flavour"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + I18n.translateToLocal("turret.upgrade.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.upgrade.scatter.a"));
                tooltip.add(I18n.translateToLocal("turret.upgrade.scatter.b"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.upgrade.stacks"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.upgrade.scatter.flavour"));
        }
    }
}

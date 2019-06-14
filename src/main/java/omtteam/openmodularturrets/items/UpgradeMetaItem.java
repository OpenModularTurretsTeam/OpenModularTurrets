package omtteam.openmodularturrets.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

@SuppressWarnings("deprecation")
public class UpgradeMetaItem extends Item {
    public final static String[] subNames = {
            OMTNames.Items.accuraccyUpgrade, OMTNames.Items.efficiencyUpgrade, OMTNames.Items.fireRateUpgrade,
            OMTNames.Items.rangeUpgrade, OMTNames.Items.scattershotUpgrade
    };

    public UpgradeMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.upgradeMetaItem);
        this.setUnlocalizedName(OMTNames.Items.upgradeMetaItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 5; i++) {
                subItems.add(new ItemStack(ModItems.upgradeMetaItem, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            switch (stack.getMetadata()) {
                case 0:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.upgrade.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.turretinfo"));
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.acc.flavour.a"));
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.acc.flavour.b"));
                    return;
                case 1:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.upgrade.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.turretinfo"));
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.eff.flavour"));
                    return;
                case 2:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.upgrade.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.turretinfo"));
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.rof.flavour"));
                    return;
                case 3:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.upgrade.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.turretinfo"));
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.range.flavour"));
                    return;
                case 4:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.upgrade.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.scatter.a"));
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.scatter.b"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.upgrade.stacks"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.turret.upgrade.scatter.flavour"));
            }
        }
    }
}

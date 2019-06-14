package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Created by Keridos on 22/07/16.
 * This Class
 */
@SuppressWarnings("deprecation")
public class ItemBlockExpander extends AbstractItemBlockBaseAddon {
    private final static String[] subNames = {
            OMTNames.Blocks.expanderInvTierOne, OMTNames.Blocks.expanderInvTierTwo, OMTNames.Blocks.expanderInvTierThree,
            OMTNames.Blocks.expanderInvTierFour, OMTNames.Blocks.expanderInvTierFive, OMTNames.Blocks.expanderPowerTierOne,
            OMTNames.Blocks.expanderPowerTierTwo, OMTNames.Blocks.expanderPowerTierThree, OMTNames.Blocks.expanderPowerTierFour,
            OMTNames.Blocks.expanderPowerTierFive,
    };

    public ItemBlockExpander(Block block) {
        super(block);
        setHasSubtypes(true);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.expander);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 10; i++) {
                subItems.add(new ItemStack(ModBlocks.expander, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack itemStack) {
        return "tile." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            switch (stack.getMetadata()) {
                case 0:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv3") + " 4.");
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.inv.1"));
                    return;
                case 1:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv3") + " 8.");
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.inv.2"));
                    return;
                case 2:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv3") + " 16.");
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.inv.3"));
                    return;
                case 3:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv3") + " 32.");
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.inv.4"));
                    return;
                case 4:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.inv1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv2"));
                    tooltip.add(TextFormatting.WHITE + safeLocalize("text.openmodularturrets.expander.inv3") + " 64.");
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.inv.5"));
                    return;
                case 5:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.power1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize(
                            "text.openmodularturrets.expander.power2") + " " + OMTConfig.MISCELLANEOUS.expanderPowerTierOneCapacity + " " + safeLocalize(
                            "text.openmodularturrets.expander.power3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.power.1"));
                    return;
                case 6:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.power1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize(
                            "text.openmodularturrets.expander.power2") + " " + OMTConfig.MISCELLANEOUS.expanderPowerTierTwoCapacity + " " + safeLocalize(
                            "text.openmodularturrets.expander.power3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.power.2"));
                    return;
                case 7:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.power1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize(
                            "text.openmodularturrets.expander.power2") + " " + OMTConfig.MISCELLANEOUS.expanderPowerTierThreeCapacity + " " + safeLocalize(
                            "text.openmodularturrets.expander.power3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.power.3"));
                    return;
                case 8:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.power1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize(
                            "text.openmodularturrets.expander.power2") + " " + OMTConfig.MISCELLANEOUS.expanderPowerTierFourCapacity + " " + safeLocalize(
                            "text.openmodularturrets.expander.power3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.power.4"));
                    return;
                case 9:
                    tooltip.add("");
                    tooltip.add(TextFormatting.GOLD + safeLocalize("text.openmodularturrets.expander.power1"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.WHITE + safeLocalize(
                            "text.openmodularturrets.expander.power2") + " " + OMTConfig.MISCELLANEOUS.expanderPowerTierFiveCapacity + " " + safeLocalize(
                            "text.openmodularturrets.expander.power3"));
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.expander.power.5"));
            }
        }
    }
}

package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Created by Keridos on 22/07/16.
 * This Class
 */
@SuppressWarnings("deprecation")
public class ItemBlockExpander extends ItemBlockBaseAddon {
    public ItemBlockExpander(Block block) {
        super(block);
        setHasSubtypes(true);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.expander);
    }

    private final static String[] subNames = {
            OMTNames.Blocks.expanderInvTierOne, OMTNames.Blocks.expanderInvTierTwo, OMTNames.Blocks.expanderInvTierThree,
            OMTNames.Blocks.expanderInvTierFour, OMTNames.Blocks.expanderInvTierFive, OMTNames.Blocks.expanderPowerTierOne,
            OMTNames.Blocks.expanderPowerTierTwo, OMTNames.Blocks.expanderPowerTierThree, OMTNames.Blocks.expanderPowerTierFour,
            OMTNames.Blocks.expanderPowerTierFive,
    };

    @Override
    @ParametersAreNonnullByDefault
    public void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 10; i++) {
            subItems.add(new ItemStack(ModBlocks.expander, 1, i));
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
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.inv1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv2"));
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv3") + " 4.");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.inv.1"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.inv1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv2"));
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv3") + " 8.");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.inv.2"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.inv1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv2"));
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv3") + " 16.");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.inv.3"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.inv1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv2"));
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv3") + " 32.");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.inv.4"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.inv1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv2"));
                tooltip.add(TextFormatting.WHITE + safeLocalize("tooltip.expander.inv3") + " 64.");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.inv.5"));
                return;
            case 5:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.power1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize(
                        "tooltip.expander.power2") + " " + ConfigHandler.getExpanderPowerTierOneCapacity() + " " + safeLocalize(
                        "tooltip.expander.power3"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.power.1"));
                return;
            case 6:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.power1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize(
                        "tooltip.expander.power2") + " " + ConfigHandler.getExpanderPowerTierTwoCapacity() + " " + safeLocalize(
                        "tooltip.expander.power3"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.power.2"));
                return;
            case 7:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.power1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize(
                        "tooltip.expander.power2") + " " + ConfigHandler.getExpanderPowerTierThreeCapacity() + " " + safeLocalize(
                        "tooltip.expander.power3"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.power.3"));
                return;
            case 8:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.power1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize(
                        "tooltip.expander.power2") + " " + ConfigHandler.getExpanderPowerTierFourCapacity() + " " + safeLocalize(
                        "tooltip.expander.power3"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.power.4"));
                return;
            case 9:
                tooltip.add("");
                tooltip.add(TextFormatting.GOLD + safeLocalize("tooltip.expander.power1"));
                tooltip.add("");
                tooltip.add(TextFormatting.WHITE + safeLocalize(
                        "tooltip.expander.power2") + " " + ConfigHandler.getExpanderPowerTierFiveCapacity() + " " + safeLocalize(
                        "tooltip.expander.power3"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.expander.power.5"));
        }
    }
}

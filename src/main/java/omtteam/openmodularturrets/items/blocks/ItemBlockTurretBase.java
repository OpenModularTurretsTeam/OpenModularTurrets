package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
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

@SuppressWarnings("deprecation")
public class ItemBlockTurretBase extends ItemBlock {
    private final static String[] subNames = {
            "_tier_one", "_tier_two", "_tier_three", "_tier_four", "_tier_five"
    };

    public ItemBlockTurretBase(Block block) {
        super(block);
        setHasSubtypes(true);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.turretBase);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 5; i++) {
                subItems.add(new ItemStack(ModBlocks.turretBase, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack itemStack) {
        return "tile." + OMTNames.Blocks.turretBase + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            tooltip.add(safeLocalize(OMTNames.Localizations.GUI.TURRET_BASE_DESCRIPTION));

            switch (stack.getMetadata()) {
                case 0:
                    tooltip.add("");
                    tooltip.add(TextFormatting.AQUA + "--" + safeLocalize("text.openmodularturrets.energy.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.max") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierOne.baseMaxCharge);
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.io") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierOne.baseMaxIo);
                    tooltip.add("");
                    tooltip.add(TextFormatting.GREEN + "--" + safeLocalize("text.openmodularturrets.extras.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.addons.0"));
                    tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                                        + OMTConfig.BASES.baseTierOne.baseMaxTurrets);
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.1"));
                    return;
                case 1:
                    tooltip.add("");
                    tooltip.add(TextFormatting.AQUA + "--" + safeLocalize("text.openmodularturrets.energy.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.max") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierTwo.baseMaxCharge);
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.io") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierTwo.baseMaxIo);
                    tooltip.add("");
                    tooltip.add(TextFormatting.GREEN + "--" + safeLocalize("text.openmodularturrets.extras.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.addons.2"));
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.upgrade.1"));
                    tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                                        + OMTConfig.BASES.baseTierTwo.baseMaxTurrets);
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.2"));
                    return;
                case 2:
                    tooltip.add("");
                    tooltip.add(TextFormatting.AQUA + "--" + safeLocalize("text.openmodularturret.energy.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.max") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierThree.baseMaxCharge);
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.io") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierThree.baseMaxIo);
                    tooltip.add("");
                    tooltip.add(TextFormatting.GREEN + "--" + safeLocalize("text.openmodularturrets.extras.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.addons.2"));
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.upgrade.1"));
                    tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                                        + OMTConfig.BASES.baseTierThree.baseMaxTurrets);
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.3"));
                    return;
                case 3:
                    tooltip.add("");
                    tooltip.add(TextFormatting.AQUA + "--" + safeLocalize("text.openmodularturrets.energy.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.max") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierFour.baseMaxCharge);
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.io") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierFour.baseMaxIo);
                    tooltip.add("");
                    tooltip.add(TextFormatting.GREEN + "--" + safeLocalize("text.openmodularturrets.extras.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.addons.2"));
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.upgrade.1"));
                    tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                                        + OMTConfig.BASES.baseTierFour.baseMaxTurrets);
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.4"));
                    return;
                case 4:
                    tooltip.add("");
                    tooltip.add(TextFormatting.AQUA + "--" + safeLocalize("text.openmodularturrets.energy.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.max") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierFive.baseMaxCharge);
                    tooltip.add(safeLocalize("text.openmodularturrets.rf.io") + ": " + TextFormatting.WHITE +
                                        OMTConfig.BASES.baseTierFive.baseMaxIo);
                    tooltip.add("");
                    tooltip.add(TextFormatting.GREEN + "--" + safeLocalize("text.openmodularturrets.extras.label") + "--");
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.addons.2"));
                    tooltip.add(safeLocalize("text.openmodularturrets.extras.upgrade.2"));
                    tooltip.add(safeLocalize(OMTNames.Localizations.Text.TURRET_LIMIT) + ": " + TextFormatting.WHITE
                                        + OMTConfig.BASES.baseTierFive.baseMaxTurrets);
                    tooltip.add("");
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.5a"));
                    tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("text.openmodularturrets.flavour.base.5b"));
            }
        }
    }
}

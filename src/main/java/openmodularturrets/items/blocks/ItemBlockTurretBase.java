package openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import openmodularturrets.init.ModBlocks;
import openmodularturrets.handler.ConfigHandler;

import java.util.List;

public class ItemBlockTurretBase extends ItemBlock {
    public ItemBlockTurretBase(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    public final static String[] subNames = {
            "TierOne", "TierTwo", "TierThree","TierFour","TierFive"
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModBlocks.turretBase, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return this.getUnlocalizedName() + subNames[itemStack.getItemDamage()];
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
                tooltip.add(EnumChatFormatting.AQUA + "--" + StatCollector.translateToLocal("tooptip.energy.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.max") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierOneMaxCharge());
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.io") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierOneMaxIo());
                tooltip.add("");
                tooltip.add(EnumChatFormatting.GREEN + "--" + StatCollector.translateToLocal("tooltip.extras.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.addons.0"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.1"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.AQUA + "--" + StatCollector.translateToLocal("tooptip.energy.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.max") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierTwoMaxCharge());
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.io") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierTwoMaxIo());
                tooltip.add("");
                tooltip.add(EnumChatFormatting.GREEN + "--" + StatCollector.translateToLocal("tooltip.extras.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.addons.2"));
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.upgrade.1"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.2"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.AQUA + "--" + StatCollector.translateToLocal("tooptip.energy.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.max") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierThreeMaxCharge());
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.io") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierThreeMaxIo());
                tooltip.add("");
                tooltip.add(EnumChatFormatting.GREEN + "--" + StatCollector.translateToLocal("tooltip.extras.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.addons.2"));
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.upgrade.1"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.3"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.AQUA + "--" + StatCollector.translateToLocal("tooptip.energy.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.max") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierFourMaxCharge());
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.io") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierFourMaxIo());
                tooltip.add("");
                tooltip.add(EnumChatFormatting.GREEN + "--" + StatCollector.translateToLocal("tooltip.extras.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.addons.2"));
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.upgrade.1"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.4"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(EnumChatFormatting.AQUA + "--" + StatCollector.translateToLocal("tooptip.energy.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.max") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierFiveMaxCharge());
                tooltip.add(StatCollector.translateToLocal("tooltip.rf.io") + ": " + EnumChatFormatting.WHITE +
                        ConfigHandler.getBaseTierFiveMaxIo());
                tooltip.add("");
                tooltip.add(EnumChatFormatting.GREEN + "--" + StatCollector.translateToLocal("tooltip.extras.label") + "--");
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.addons.2"));
                tooltip.add(StatCollector.translateToLocal("tooltip.extras.upgrade.2"));
                tooltip.add("");
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.5a"));
                tooltip.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.base.5b"));
        }
    }
}

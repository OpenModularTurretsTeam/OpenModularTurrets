package openmodularturrets.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class ItemBlockExpanderInvTierTwo extends ItemBlock {

    public ItemBlockExpanderInvTierTwo(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.expander.inv1"));
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("tooltip.expander.inv2"));
        p_77624_3_.add(EnumChatFormatting.WHITE + StatCollector.translateToLocal("tooltip.expander.inv3") + " 8.");
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("flavour.expander.inv.2"));
    }
}

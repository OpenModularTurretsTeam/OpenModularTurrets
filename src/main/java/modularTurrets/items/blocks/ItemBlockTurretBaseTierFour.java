package modularTurrets.items.blocks;

import modularTurrets.misc.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockTurretBaseTierFour extends ItemBlock {
    public ItemBlockTurretBaseTierFour(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A7b--Energy--");
        p_77624_3_.add("Max Capacity: "+ ConfigHandler.getBaseTierFourMaxCharge());
        p_77624_3_.add("Max IO: "+ ConfigHandler.getBaseTierFourMaxIo());
        p_77624_3_.add("");
        p_77624_3_.add("\u00A72--Extras--");
        p_77624_3_.add("2x Addon Slots");
        p_77624_3_.add("2x Upgrade Slot");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78All the kings horses, and");
        p_77624_3_.add("\u00A78all the king's men...");
    }
}

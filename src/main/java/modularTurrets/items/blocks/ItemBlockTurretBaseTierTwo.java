package modularTurrets.items.blocks;

import modularTurrets.misc.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockTurretBaseTierTwo extends ItemBlock {
    public ItemBlockTurretBaseTierTwo(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A7b--Energy--");
        p_77624_3_.add("Max Capacity: "+ ConfigHandler.getBaseTierTwoMaxCharge());
        p_77624_3_.add("Max IO: "+ ConfigHandler.getBaseTierTwoMaxIo());
        p_77624_3_.add("");
        p_77624_3_.add("\u00A72--Extras--");
        p_77624_3_.add("2x Addon Slots");
        p_77624_3_.add("1x Upgrade Slot");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78I said good day sir!!");
    }
}

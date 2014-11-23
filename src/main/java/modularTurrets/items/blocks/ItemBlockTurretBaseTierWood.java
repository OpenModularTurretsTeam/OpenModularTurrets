package modularTurrets.items.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import modularTurrets.misc.ConfigHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockTurretBaseTierWood extends ItemBlock {
    public ItemBlockTurretBaseTierWood(Block p_i45328_1_) {
        super(p_i45328_1_);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add("\u00A7b--Energy--");
        p_77624_3_.add("Max Capacity: "+ ConfigHandler.getBaseTierWoodMaxCharge());
        p_77624_3_.add("Max IO: "+ ConfigHandler.getBaseTierWoodMaxIo());
        p_77624_3_.add("");
        p_77624_3_.add("\u00A72--Extras--");
        p_77624_3_.add("None");
        p_77624_3_.add("");
        p_77624_3_.add("\u00A78The powder's dry, and I'm homeless.");
    }
}

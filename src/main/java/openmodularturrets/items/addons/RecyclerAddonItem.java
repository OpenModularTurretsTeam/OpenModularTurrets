package openmodularturrets.items.addons;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.Names;

import java.util.List;

public class RecyclerAddonItem extends AddonItem {
    public RecyclerAddonItem() {
        super();

        this.setUnlocalizedName(Names.Items.unlocalisedRecyclerAddon);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List p_77624_3_, boolean p_77624_4_) {
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.RED + StatCollector.translateToLocal("turret.addon.label"));
        p_77624_3_.add("");
        p_77624_3_.add(StatCollector.translateToLocal("turret.addon.recycler.1"));
        p_77624_3_.add(StatCollector.translateToLocal("turret.addon.recycler.2"));
        p_77624_3_.add("");
        p_77624_3_.add(StatCollector.translateToLocal(
                "turret.addon.recycler.3") + " " + ConfigHandler.getRecyclerNegateChance() + "%");
        p_77624_3_.add(StatCollector.translateToLocal(
                "turret.addon.recycler.4") + " " + ConfigHandler.getRecyclerAddChance() + "%");
        p_77624_3_.add("");
        p_77624_3_.add(EnumChatFormatting.DARK_GRAY + StatCollector.translateToLocal("turret.addon.recycler.flavour"));
    }
}
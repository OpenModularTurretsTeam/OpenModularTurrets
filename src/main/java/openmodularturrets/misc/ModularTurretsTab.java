package openmodularturrets.misc;

import openmodularturrets.blocks.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModularTurretsTab extends CreativeTabs {

    public ModularTurretsTab(String label) {
        super(label);
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(Blocks.machineGunTurret);
    }

    @Override
    public Item getTabIconItem() {
        return null;
    }
}

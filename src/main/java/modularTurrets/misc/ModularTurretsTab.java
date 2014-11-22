package modularTurrets.misc;

import modularTurrets.blocks.Blocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ModularTurretsTab extends CreativeTabs {

    public ModularTurretsTab(String label) {
        super(label);
    }

    @Override
    public Item getTabIconItem() {
        return new ItemBlock(Blocks.machineGunTurret);
    }
}

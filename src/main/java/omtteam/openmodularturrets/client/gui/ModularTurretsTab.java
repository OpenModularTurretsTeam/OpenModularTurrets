package omtteam.openmodularturrets.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.blocks.ItemBlockLaserTurret;

@MethodsReturnNonnullByDefault
public class ModularTurretsTab extends CreativeTabs {
    @SuppressWarnings("SameParameterValue")
    public ModularTurretsTab(String label) {
        super(label);
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ModBlocks.laserTurret);
    }

    @Override
    public Item getTabIconItem() {
        return new ItemBlockLaserTurret(ModBlocks.laserTurret);
    }
}

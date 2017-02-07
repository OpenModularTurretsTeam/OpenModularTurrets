package omtteam.openmodularturrets.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.omlib.compatability.minecraft.CompatCreativeTabs;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.blocks.ItemBlockLaserTurret;

@MethodsReturnNonnullByDefault
public class ModularTurretsTab extends CompatCreativeTabs {
    @SuppressWarnings("SameParameterValue")
    public ModularTurretsTab(String label) {
        super(label);
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ModBlocks.laserTurret);
    }

    @Override
    public Item getItem() {
        return new ItemBlockLaserTurret(ModBlocks.laserTurret);
    }
}

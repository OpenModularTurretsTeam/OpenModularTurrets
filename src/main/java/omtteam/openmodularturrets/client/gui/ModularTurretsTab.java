package omtteam.openmodularturrets.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.omlib.compatability.minecraft.CompatCreativeTabs;
import omtteam.openmodularturrets.init.ModBlocks;
import omtteam.openmodularturrets.items.blocks.ItemBlockLaserTurret;
import omtteam.openmodularturrets.reference.Reference;


@MethodsReturnNonnullByDefault
public class ModularTurretsTab extends CompatCreativeTabs {
    private static ModularTurretsTab instance;

    private ModularTurretsTab(String label) {
        super(label);
    }

    public static ModularTurretsTab getInstance() {
        if (instance == null) {
            instance = new ModularTurretsTab(Reference.MOD_ID);
        }
        return instance;
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

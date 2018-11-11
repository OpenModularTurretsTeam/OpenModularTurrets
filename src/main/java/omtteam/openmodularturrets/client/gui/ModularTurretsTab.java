package omtteam.openmodularturrets.client.gui;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.Reference;

@MethodsReturnNonnullByDefault
public final class ModularTurretsTab extends CreativeTabs {
    private static ModularTurretsTab instance;

    @SuppressWarnings("SameParameterValue")
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
    public ItemStack getTabIconItem() {
        return new ItemStack(getItem(), 1, 4);
    }

    public Item getItem() {
        return ModItems.ammoMetaItem;
    }
}

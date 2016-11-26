package openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openmodularturrets.ModularTurrets;
import openmodularturrets.init.ModItems;
import openmodularturrets.reference.Names;

import java.util.List;

public class AmmoMetaItem extends Item {
    public AmmoMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }

    public final static String[] subNames = {
            Names.Items.blazingClayItem, Names.Items.bulletCraftableItem, Names.Items.ferroSlug,
            Names.Items.grenadeCraftableItem, Names.Items.rocketCraftableItem
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModItems.addonMetaItem, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}

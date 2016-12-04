package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;

import java.util.List;

public class ThrowableMetaItem extends Item {
    public ThrowableMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, Names.Items.throwableMetaItem);
        this.setUnlocalizedName(Names.Items.throwableMetaItem);
    }

    public final static String[] subNames = {
            Names.Items.bulletThrowableItem, Names.Items.grenadeThrowableItem
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 2; i++) {
            subItems.add(new ItemStack(ModItems.throwableMetaItem, 1, i));
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

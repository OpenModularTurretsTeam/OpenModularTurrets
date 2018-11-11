package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class IntermediateProductRegular extends Item {
    public final static String[] subNames = {
            OMTNames.Items.ioBus
    };

    public IntermediateProductRegular() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.intermediateRegularItem);
        this.setUnlocalizedName(OMTNames.Items.intermediateRegularItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 1; i++) {
                subItems.add(new ItemStack(ModItems.intermediateProductRegular, 1, i));
            }
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.openmodularturrets.ModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.Names;

import java.util.List;

public class IntermediateProductTiered extends Item {
    public IntermediateProductTiered() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(ModularTurrets.modularTurretsTab);
    }

    public final static String[] subNames = {
            Names.Items.sensorTierOne, Names.Items.sensorTierTwo, Names.Items.sensorTierThree,
            Names.Items.sensorTierFour, Names.Items.sensorTierFive, Names.Items.chamberTierOne,
            Names.Items.chamberTierTwo, Names.Items.chamberTierThree, Names.Items.chamberTierFour,
            Names.Items.chamberTierFive, Names.Items.barrelTierOne, Names.Items.barrelTierTwo,
            Names.Items.barrelTierThree, Names.Items.barrelTierFour, Names.Items.barrelTierFive,
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 15; i++) {
            subItems.add(new ItemStack(ModItems.intermediateProductTiered, 1, i));
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
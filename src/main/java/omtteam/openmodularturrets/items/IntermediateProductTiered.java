package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import omtteam.omlib.item.CompatItem;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class IntermediateProductTiered extends CompatItem {
    public IntermediateProductTiered() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.intermediateTieredItem);
        this.setUnlocalizedName(OMTNames.Items.intermediateTieredItem);
    }

    public final static String[] subNames = {
            OMTNames.Items.sensorTierOne, OMTNames.Items.sensorTierTwo, OMTNames.Items.sensorTierThree,
            OMTNames.Items.sensorTierFour, OMTNames.Items.sensorTierFive, OMTNames.Items.chamberTierOne,
            OMTNames.Items.chamberTierTwo, OMTNames.Items.chamberTierThree, OMTNames.Items.chamberTierFour,
            OMTNames.Items.chamberTierFive, OMTNames.Items.barrelTierOne, OMTNames.Items.barrelTierTwo,
            OMTNames.Items.barrelTierThree, OMTNames.Items.barrelTierFour, OMTNames.Items.barrelTierFive,
    };

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 15; i++) {
            subItems.add(new ItemStack(ModItems.intermediateProductTiered, 1, i));
        }
    }

    @Override
    @Nonnull
    public String getUnlocalizedName(ItemStack itemStack) {
        return "item." + subNames[itemStack.getItemDamage()];
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }
}
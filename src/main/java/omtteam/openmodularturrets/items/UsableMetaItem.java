package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;

import java.util.List;

import static omtteam.omlib.util.GeneralUtil.getBooleanLocalization;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class UsableMetaItem extends Item {
    public UsableMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, Names.Items.usableMetaItem);
        this.setUnlocalizedName(Names.Items.usableMetaItem);
    }

    public final static String[] subNames = {
            Names.Items.bulletThrowableItem, Names.Items.grenadeThrowableItem, Names.Items.memoryCard
    };


    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IBlockAccess world, BlockPos pos, EntityPlayer player) {
        if (stack.getItemDamage() == 2) {
            return true;
        }
        return super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 3; i++) {
            subItems.add(new ItemStack(ModItems.usableMetaItem, 1, i));
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

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player,
                               List tooltip, boolean isAdvanced) {
        if (stack.hasTagCompound()) {
            // StatCollector is a class which allows us to handle string
            // language translation. This requires that you fill out the
            // translation in you language class.
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
            tooltip.add(safeLocalize(Names.Localizations.INVERT + ": " + getBooleanLocalization(stack.getTagCompound().getBoolean("inverted"))));
        } else // If the brain does not have valid tag data, a default message
        {
            tooltip.add(safeLocalize(
                    "tooltip.openmodularturrets.memoryCard.desc"));

        }
    }
}

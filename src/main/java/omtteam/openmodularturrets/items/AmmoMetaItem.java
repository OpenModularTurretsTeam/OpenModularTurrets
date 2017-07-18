package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.compatibility.minecraft.CompatItem;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class AmmoMetaItem extends CompatItem {
    public AmmoMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.ammoMetaItem);
        this.setUnlocalizedName(OMTNames.Items.ammoMetaItem);
    }

    public final static String[] subNames = {
            OMTNames.Items.blazingClayItem, OMTNames.Items.bulletCraftableItem, OMTNames.Items.ferroSlug,
            OMTNames.Items.grenadeCraftableItem, OMTNames.Items.rocketCraftableItem
    };

    @Override
    @ParametersAreNonnullByDefault
    public void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 5; i++) {
            subItems.add(new ItemStack(ModItems.ammoMetaItem, 1, i));
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

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean isAdvanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + safeLocalize("turret.ammo.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.ammo.blazing_clay"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + safeLocalize("turret.ammo.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.ammo.bullet"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + safeLocalize("turret.ammo.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.ammo.ferro_slug"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + safeLocalize("turret.ammo.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.ammo.grenade"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(TextFormatting.BLUE + safeLocalize("turret.ammo.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.ammo.rocket"));
        }
    }
}

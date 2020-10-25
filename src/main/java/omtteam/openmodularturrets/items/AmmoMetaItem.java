package omtteam.openmodularturrets.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.GeneralUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

public class AmmoMetaItem extends Item {
    public final static String[] subNames = {
            OMTNames.Items.blazingClayItem, OMTNames.Items.bulletCraftableItem, OMTNames.Items.ferroSlug,
            OMTNames.Items.grenadeCraftableItem, OMTNames.Items.rocketCraftableItem, OMTNames.Items.fakeDisposableItem
    };

    public AmmoMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.ammoMetaItem);
        this.setUnlocalizedName(OMTNames.Items.ammoMetaItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> subItems) {
        if (isInCreativeTab(itemIn)) {
            for (int i = 0; i < 6; i++) {
                subItems.add(new ItemStack(ModItems.ammoMetaItem, 1, i));
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

    @SideOnly(Side.CLIENT)
    @Override
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, @Nullable World playerIn, List<String> tooltip, ITooltipFlag advanced) {
        if (!(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) {
            tooltip.add(GeneralUtil.getShiftDetail());
        } else {
            switch (stack.getMetadata()) {
                case 0:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo.blazing_clay"));
                    return;
                case 1:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo.bullet"));
                    return;
                case 2:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo.ferro_slug"));
                    return;
                case 3:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo.grenade"));
                    return;
                case 4:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add(safeLocalize("text.openmodularturrets.turret.ammo.rocket"));
                case 5:
                    tooltip.add("");
                    tooltip.add(TextFormatting.BLUE + safeLocalize("text.openmodularturrets.turret.ammo.label"));
                    tooltip.add("");
                    tooltip.add("fake item, do not use");
            }
        }
    }
}

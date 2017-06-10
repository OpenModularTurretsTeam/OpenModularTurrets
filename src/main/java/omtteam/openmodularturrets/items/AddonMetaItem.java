package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import omtteam.omlib.compatability.minecraft.CompatItem;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

@SuppressWarnings("deprecation")
public class AddonMetaItem extends CompatItem {
    public AddonMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Items.addonMetaItem);
        this.setUnlocalizedName(OMTNames.Items.addonMetaItem);
    }

    public final static String[] subNames = {
            OMTNames.Items.concealerAddon, OMTNames.Items.damageAmpAddon, OMTNames.Items.potentiaAddon,
            OMTNames.Items.recyclerAddon, OMTNames.Items.redReactorAddon, OMTNames.Items.serialPortAddon,
            OMTNames.Items.solarPanelAddon, OMTNames.Items.fakeDropsAddon
    };

    @Override
    @ParametersAreNonnullByDefault
    public void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 8; i++) {
            subItems.add(new ItemStack(ModItems.addonMetaItem, 1, i));
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

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.concealer.1"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.concealer.flavour"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.amp.a.label"));
                tooltip.add(safeLocalize("turret.addon.amp.b.label"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.amp.flavour"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize(
                        "turret.addon.potentia.a") + ConfigHandler.getPotentiaToRFRatio() + " " + safeLocalize(
                        "turret.addon.potentia.b"));
                tooltip.add("");
                tooltip.add(safeLocalize(
                        "turret.addon.potentia.c") + ConfigHandler.getPotentiaAddonCapacity() + ".");
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.potentia.d"));
                tooltip.add(safeLocalize("turret.addon.potentia.e"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.potentia.flavour"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.recycler.1"));
                tooltip.add(safeLocalize("turret.addon.recycler.2"));
                tooltip.add("");
                tooltip.add(safeLocalize(
                        "turret.addon.recycler.3") + " " + ConfigHandler.getRecyclerNegateChance() + "%");
                tooltip.add(safeLocalize(
                        "turret.addon.recycler.4") + " " + ConfigHandler.getRecyclerAddChance() + "%");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.recycler.flavour"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize(
                        "turret.addon.redstone.a") + " " + ConfigHandler.getRedstoneReactorAddonGen() + " " + safeLocalize(
                        "turret.addon.redstone.b"));
                tooltip.add(safeLocalize("turret.addon.redstone.c"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.redstone.flavour"));
                return;
            case 5:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.serial.a") + " " + safeLocalize(
                        "turret.addon.serial.b"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.serial.flavour"));
                return;
            case 6:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize(
                        "turret.addon.solar.a") + " " + ConfigHandler.getSolarPanelAddonGen() + " " + safeLocalize(
                        "turret.addon.solar.b"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.solar.flavour"));
            case 7:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + safeLocalize("turret.addon.label"));
                tooltip.add("");
                tooltip.add(safeLocalize("turret.addon.fakedrops.a"));
                tooltip.add(safeLocalize("turret.addon.fakedrops.b"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("turret.addon.fakedrops.flavour"));
        }
    }
}

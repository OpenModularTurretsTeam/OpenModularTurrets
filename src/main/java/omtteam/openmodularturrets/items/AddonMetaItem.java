package omtteam.openmodularturrets.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.reference.Names;

import java.util.List;

public class AddonMetaItem extends Item {
    public AddonMetaItem() {
        super();

        this.setHasSubtypes(true);
        this.setCreativeTab(OpenModularTurrets.modularTurretsTab);
    }

    public final static String[] subNames = {
            Names.Items.concealerAddon, Names.Items.damageAmpAddon, Names.Items.potentiaAddon,
            Names.Items.recyclerAddon, Names.Items.redReactorAddon, Names.Items.serialPortAddon,
            Names.Items.solarPanelAddon
    };

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < 7; i++) {
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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        switch (stack.getMetadata()) {
            case 0:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.addon.concealer.1"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.concealer.flavour"));
                return;
            case 1:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.addon.amp.a.label"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.amp.flavour"));
                return;
            case 2:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.potentia.a") + ConfigHandler.getPotentiaToRFRatio() + " " + I18n.translateToLocal(
                        "turret.addon.potentia.b"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.potentia.c") + ConfigHandler.getPotentiaAddonCapacity() + ".");
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.addon.potentia.d"));
                tooltip.add(I18n.translateToLocal("turret.addon.potentia.e"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.potentia.flavour"));
                return;
            case 3:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.addon.recycler.1"));
                tooltip.add(I18n.translateToLocal("turret.addon.recycler.2"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.recycler.3") + " " + ConfigHandler.getRecyclerNegateChance() + "%");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.recycler.4") + " " + ConfigHandler.getRecyclerAddChance() + "%");
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.recycler.flavour"));
                return;
            case 4:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.redstone.a") + " " + ConfigHandler.getRedstoneReactorAddonGen() + " " + I18n.translateToLocal(
                        "turret.addon.redstone.b"));
                tooltip.add(I18n.translateToLocal("turret.addon.redstone.c"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.redstone.flavour"));
                return;
            case 5:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal("turret.addon.serial.a") + " " + I18n.translateToLocal(
                        "turret.addon.serial.b"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.serial.flavour"));
                return;
            case 6:
                tooltip.add("");
                tooltip.add(TextFormatting.RED + I18n.translateToLocal("turret.addon.label"));
                tooltip.add("");
                tooltip.add(I18n.translateToLocal(
                        "turret.addon.solar.a") + " " + ConfigHandler.getSolarPanelAddonGen() + " " + I18n.translateToLocal(
                        "turret.addon.solar.b"));
                tooltip.add("");
                tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("turret.addon.solar.flavour"));
        }
    }
}

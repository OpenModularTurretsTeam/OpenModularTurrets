package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.reference.Names;
import omtteam.openmodularturrets.reference.Reference;

import java.text.DecimalFormat;
import java.util.List;

public class ItemBlockGrenadeLauncherTurret extends ItemBlock {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockGrenadeLauncherTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, Names.Blocks.grenadeTurret);
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + I18n.translateToLocal("tooltip.info") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.tier") + ": " + TextFormatting.WHITE + "3");
        tooltip.add(I18n.translateToLocal("tooltip.range") + ": " + TextFormatting.WHITE +
                               ConfigHandler.getGrenadeTurretSettings().getRange());
        tooltip.add(I18n.translateToLocal("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                               I18n.translateToLocal("turret.accuracy.medium"));
        tooltip.add(I18n.translateToLocal("tooltip.ammo") + ": " + TextFormatting.WHITE +
                               I18n.translateToLocal("turret.ammo.2"));
        tooltip.add(I18n.translateToLocal("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                               I18n.translateToLocal("base.tier.3"));
        tooltip.add("");
        tooltip.add(
                TextFormatting.DARK_PURPLE + "--" + I18n.translateToLocal("tooltip.damage.label") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                               (ConfigHandler.getGrenadeTurretSettings().getDamage() / 2F) + " " + I18n.translateToLocal(
                "tooltip.health"));
        tooltip.add(I18n.translateToLocal("tooltip.aoe") + ": 3");
        tooltip.add(I18n.translateToLocal("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                20.0F / ConfigHandler.getGrenadeTurretSettings().getFireRate()));
        tooltip.add(I18n.translateToLocal("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                               ConfigHandler.getGrenadeTurretSettings().getPowerUsage() + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("flavour.turret.2a"));
        tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("flavour.turret.2b"));
    }
}

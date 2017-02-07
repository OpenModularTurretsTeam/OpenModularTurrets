package omtteam.openmodularturrets.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import omtteam.omlib.compatability.minecraft.CompatItemBlock;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemBlockIncendiaryTurret extends CompatItemBlock {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockIncendiaryTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.incendiaryTurret);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + I18n.translateToLocal("tooltip.info") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.tier") + ": " + TextFormatting.WHITE + "2");
        tooltip.add(I18n.translateToLocal("tooltip.range") + ": " + TextFormatting.WHITE +
                ConfigHandler.getIncendiary_turret().getRange());
        tooltip.add(I18n.translateToLocal("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("turret.accuracy.medium"));
        tooltip.add(I18n.translateToLocal("tooltip.ammo") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("turret.ammo.7"));
        tooltip.add(I18n.translateToLocal("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                I18n.translateToLocal("base.tier.2"));
        tooltip.add("");
        tooltip.add(
                TextFormatting.DARK_PURPLE + "--" + I18n.translateToLocal("tooltip.damage.label") + "--");
        tooltip.add(I18n.translateToLocal("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                (ConfigHandler.getIncendiary_turret().getDamage() / 2F) + " " + I18n.translateToLocal(
                "tooltip.health"));
        tooltip.add(I18n.translateToLocal("tooltip.aoe") + ": " + TextFormatting.WHITE + "5");
        tooltip.add(I18n.translateToLocal("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                20.0F / ConfigHandler.getIncendiary_turret().getFireRate()));
        tooltip.add(I18n.translateToLocal("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                ConfigHandler.getIncendiary_turret().getPowerUsage() + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + I18n.translateToLocal("flavour.turret.7"));
    }
}

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

import static omtteam.omlib.util.GeneralUtil.safeLocalize;

@SuppressWarnings("deprecation")
public class ItemBlockTeleporterTurret extends CompatItemBlock {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockTeleporterTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.teleporterTurret);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + safeLocalize("tooltip.info") + "--");
        tooltip.add(safeLocalize("tooltip.tier") + ": " + TextFormatting.WHITE + "4");
        tooltip.add(safeLocalize("tooltip.range") + ": " + TextFormatting.WHITE +
                ConfigHandler.getTeleporter_turret().getRange());
        tooltip.add(safeLocalize("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                safeLocalize("turret.accuracy.high"));
        tooltip.add(safeLocalize("tooltip.ammo") + ": " + TextFormatting.WHITE +
                safeLocalize("turret.ammo.4"));
        tooltip.add(safeLocalize("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                safeLocalize("base.tier.4"));
        tooltip.add("");
        tooltip.add(
                TextFormatting.DARK_PURPLE + "--" + safeLocalize("tooltip.damage.label") + "--");
        tooltip.add(safeLocalize("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                (ConfigHandler.getTeleporter_turret().getDamage() / 2F) + " " + I18n.translateToLocal(
                "tooltip.health"));
        tooltip.add(safeLocalize("tooltip.aoe") + ": " + TextFormatting.WHITE + "0");
        tooltip.add(safeLocalize("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                20.0F / ConfigHandler.getTeleporter_turret().getFireRate()));
        tooltip.add(safeLocalize("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                ConfigHandler.getTeleporter_turret().getPowerUsage() + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.turret.9a"));
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.turret.9b"));
    }
}

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
public class ItemBlockGrenadeLauncherTurret extends CompatItemBlock {
    private static final DecimalFormat df = new DecimalFormat("0.0");

    public ItemBlockGrenadeLauncherTurret(Block block) {
        super(block);
        this.setRegistryName(Reference.MOD_ID, OMTNames.Blocks.grenadeTurret);
    }

    @Override
    @SuppressWarnings("unchecked")
    @ParametersAreNonnullByDefault
    public void addInformation(ItemStack itemStack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "--" + safeLocalize("tooltip.info") + "--");
        tooltip.add(safeLocalize("tooltip.tier") + ": " + TextFormatting.WHITE + "3");
        tooltip.add(safeLocalize("tooltip.range") + ": " + TextFormatting.WHITE +
                ConfigHandler.getGrenadeTurretSettings().getRange());
        tooltip.add(safeLocalize("tooltip.accuracy") + ": " + TextFormatting.WHITE +
                safeLocalize("turret.accuracy.medium"));
        tooltip.add(safeLocalize("tooltip.ammo") + ": " + TextFormatting.WHITE +
                safeLocalize("turret.ammo.2"));
        tooltip.add(safeLocalize("tooltip.tier_required") + ": " + TextFormatting.WHITE +
                safeLocalize("base.tier.3"));
        tooltip.add("");
        tooltip.add(
                TextFormatting.DARK_PURPLE + "--" + safeLocalize("tooltip.damage.label") + "--");
        tooltip.add(safeLocalize("tooltip.damage.stat") + ": " + TextFormatting.WHITE +
                (ConfigHandler.getGrenadeTurretSettings().getDamage() / 2F) + " " + I18n.translateToLocal(
                "tooltip.health"));
        tooltip.add(safeLocalize("tooltip.aoe") + ": 3");
        tooltip.add(safeLocalize("tooltip.fire_rate") + ": " + TextFormatting.WHITE + df.format(
                20.0F / ConfigHandler.getGrenadeTurretSettings().getFireRate()));
        tooltip.add(safeLocalize("tooltip.energy.stat") + ": " + TextFormatting.WHITE +
                ConfigHandler.getGrenadeTurretSettings().getPowerUsage() + " RF");
        tooltip.add("");
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.turret.2a"));
        tooltip.add(TextFormatting.DARK_GRAY + safeLocalize("flavour.turret.2b"));
    }
}

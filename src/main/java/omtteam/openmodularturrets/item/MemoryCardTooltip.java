package omtteam.openmodularturrets.item;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.core.card.MemoryCardData;
import omtteam.omlib.core.security.TrustedPlayer;
import omtteam.omlib.reference.OMLibNames;
import omtteam.omlib.util.EnumMachineMode;
import omtteam.openmodularturrets.reference.OMTNames;

import javax.annotation.Nullable;
import java.util.List;

import static omtteam.omlib.util.GeneralUtil.getColoredBooleanLocalizationYesNo;
import static omtteam.omlib.util.GeneralUtil.getMachineModeLocalization;
import static omtteam.omlib.util.GeneralUtil.safeLocalize;

/**
 * Memory card tooltip while shift is held (1.20.1+ {@code MemoryCardTooltip}): the usage and the stored settings.
 */
@SideOnly(Side.CLIENT)
final class MemoryCardTooltip {
    private static final String TOOLTIP = "tooltip.openmodularturrets.memory_card.";
    /** Trusted players listed before the rest is summarized. */
    private static final int MAX_LISTED_PLAYERS = 8;

    private MemoryCardTooltip() {
    }

    static void append(@Nullable MemoryCardData data, List<String> lines) {
        for (String usage : new String[]{"desc1", "desc2", "desc3"}) {
            lines.add(TextFormatting.GRAY + safeLocalize(TOOLTIP + usage));
        }
        lines.add("");
        if (data == null) {
            lines.add(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + safeLocalize(TOOLTIP + "empty"));
            return;
        }
        lines.add(TextFormatting.AQUA + safeLocalize(TOOLTIP + "source." + data.source().name().toLowerCase()));
        MemoryCardData.BaseSettings base = data.base();
        if (base != null) {
            lines.add("");
            lines.add(TextFormatting.GOLD + safeLocalize(TOOLTIP + "base.label"));
            lines.add(line(safeLocalize(OMLibNames.Localizations.GUI.MODE),
                    getMachineModeLocalization(EnumMachineMode.fromCore(base.mode()))));
            lines.add(line(safeLocalize(OMTNames.Localizations.GUI.MULTI_TARGETING),
                    getColoredBooleanLocalizationYesNo(base.multiTargeting())));
            lines.add(line(safeLocalize(OMLibNames.Localizations.GUI.ATTACK_MOBS),
                    getColoredBooleanLocalizationYesNo(base.targetMobs())));
            lines.add(line(safeLocalize(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS),
                    getColoredBooleanLocalizationYesNo(base.targetPassive())));
            lines.add(line(safeLocalize(OMLibNames.Localizations.GUI.ATTACK_PLAYERS),
                    getColoredBooleanLocalizationYesNo(base.targetPlayers())));
            lines.add(line(safeLocalize(TOOLTIP + "range"), TextFormatting.WHITE + String.valueOf(base.range())));
        }
        List<TrustedPlayer> trusted = data.trust().trustedPlayers();
        lines.add("");
        lines.add(TextFormatting.GOLD + safeLocalize(TOOLTIP + "trust.label") + ": " + TextFormatting.WHITE
                + trusted.size());
        for (TrustedPlayer player : trusted.subList(0, Math.min(MAX_LISTED_PLAYERS, trusted.size()))) {
            lines.add(TextFormatting.GRAY + "  " + player.getName());
        }
        if (trusted.size() > MAX_LISTED_PLAYERS) {
            lines.add(TextFormatting.DARK_GRAY
                    + I18n.format(TOOLTIP + "more_players", trusted.size() - MAX_LISTED_PLAYERS));
        }
        MemoryCardData.CamoSettings camo = data.camo();
        if (camo != null) {
            lines.add("");
            lines.add(TextFormatting.GOLD + safeLocalize(TOOLTIP + "camo.label"));
            lines.add(line(safeLocalize("text.omlib.camo.disguise"), disguiseName(camo.disguise())));
            if (camo.light() != null) {
                lines.add(line(safeLocalize("text.omlib.camo.light_value"),
                        TextFormatting.WHITE + String.valueOf(camo.light().value())));
                lines.add(line(safeLocalize("text.omlib.camo.light_opacity"),
                        TextFormatting.WHITE + String.valueOf(camo.light().opacity())));
            }
        }
    }

    private static String line(String label, String value) {
        return TextFormatting.GRAY + label + ": " + value;
    }

    /** The item name of a stored disguise ({@code registry@meta}), or "none". */
    private static String disguiseName(@Nullable String disguise) {
        if (disguise == null) {
            return TextFormatting.WHITE + safeLocalize(TOOLTIP + "no_disguise");
        }
        int at = disguise.lastIndexOf('@');
        Block block = ForgeRegistries.BLOCKS.getValue(
                new ResourceLocation(at < 0 ? disguise : disguise.substring(0, at)));
        int meta = 0;
        try {
            meta = at < 0 ? 0 : Integer.parseInt(disguise.substring(at + 1));
        } catch (NumberFormatException ignored) {
            // shown with metadata 0
        }
        return TextFormatting.WHITE + (block == null ? disguise : new ItemStack(block, 1, meta).getDisplayName());
    }
}

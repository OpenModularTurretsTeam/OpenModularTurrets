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
    /** OMLib's access level names, followed by {@code AccessLevel.getSerializedName()}. */
    private static final String ACCESS_LEVEL = "gui.omtteam.omlib:access_level_";

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
        if (data.base() != null) {
            appendBase(data.base(), lines);
        }
        appendTrust(data.trust(), lines);
        if (data.camo() != null) {
            appendCamo(data.camo(), lines);
        }
    }

    private static void appendBase(MemoryCardData.BaseSettings base, List<String> lines) {
        lines.add("");
        lines.add(heading("base"));
        lines.add(line(OMLibNames.Localizations.GUI.MODE,
                TextFormatting.GREEN + getMachineModeLocalization(EnumMachineMode.fromCore(base.mode()))));
        lines.add(line(OMTNames.Localizations.GUI.TARGET, TextFormatting.WHITE + safeLocalize(
                base.multiTargeting() ? OMTNames.Localizations.GUI.MULTI : OMTNames.Localizations.GUI.SINGLE)));
        lines.add(line(OMLibNames.Localizations.GUI.ATTACK_MOBS, getColoredBooleanLocalizationYesNo(base.targetMobs())));
        lines.add(line(OMLibNames.Localizations.GUI.ATTACK_NEUTRALS,
                getColoredBooleanLocalizationYesNo(base.targetPassive())));
        lines.add(line(OMLibNames.Localizations.GUI.ATTACK_PLAYERS,
                getColoredBooleanLocalizationYesNo(base.targetPlayers())));
        lines.add(line(OMTNames.Localizations.GUI.RANGE, TextFormatting.WHITE + String.valueOf(base.range())));
    }

    private static void appendTrust(MemoryCardData.TrustSettings settings, List<String> lines) {
        lines.add("");
        lines.add(heading("trust"));
        List<TrustedPlayer> trusted = settings.trustedPlayers();
        lines.add(line(OMLibNames.Localizations.GUI.TRUSTED_PLAYERS, TextFormatting.WHITE + String.valueOf(trusted.size())));
        for (TrustedPlayer player : trusted.subList(0, Math.min(MAX_LISTED_PLAYERS, trusted.size()))) {
            lines.add(TextFormatting.GRAY + "  " + player.getName() + " " + TextFormatting.DARK_GRAY
                    + safeLocalize(ACCESS_LEVEL + player.getAccessLevel().getSerializedName()));
        }
        if (trusted.size() > MAX_LISTED_PLAYERS) {
            lines.add(TextFormatting.DARK_GRAY
                    + I18n.format(TOOLTIP + "more_players", trusted.size() - MAX_LISTED_PLAYERS));
        }
    }

    private static void appendCamo(MemoryCardData.CamoSettings camo, List<String> lines) {
        lines.add("");
        lines.add(heading("camo"));
        lines.add(line("text.omlib.camo.disguise", disguiseName(camo.disguise())));
        if (camo.light() != null) {
            lines.add(line("text.omlib.camo.light_value", TextFormatting.WHITE + String.valueOf(camo.light().value())));
            lines.add(line("text.omlib.camo.light_opacity",
                    TextFormatting.WHITE + String.valueOf(camo.light().opacity())));
        }
    }

    /** A yellow "--Section--" heading. */
    private static String heading(String section) {
        return TextFormatting.YELLOW + "--" + safeLocalize(TOOLTIP + section + ".label") + "--";
    }

    /** Gold "Label: " followed by the value, which brings its own colour. */
    private static String line(String labelKey, String value) {
        return TextFormatting.GOLD + safeLocalize(labelKey) + ": " + value;
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

package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.core.card.MemoryCardData;
import omtteam.openmodularturrets.core.security.TrustedPlayer;
import omtteam.openmodularturrets.item.MemoryCardItem;

import java.util.List;
import java.util.Optional;

/**
 * Memory card tooltip, ported from 1.12.2 {@code UsableMetaItem.addInformation}: the usage and the stored settings
 * are shown while shift is held, otherwise only the hint that shift shows more.
 */
public final class MemoryCardTooltip {
    private static final String TOOLTIP = "tooltip.openmodularturrets.memory_card.";
    private static final String GUI = "gui.openmodularturrets.";
    /** Trusted players listed before the rest is summarized. */
    private static final int MAX_LISTED_PLAYERS = 8;

    private MemoryCardTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof MemoryCardItem)) return;
        if (!Screen.hasShiftDown()) {
            lines.add(TurretBaseTooltip.shiftHint());
            return;
        }
        for (String usage : List.of("desc1", "desc2", "desc3")) {
            lines.add(Component.translatable(TOOLTIP + usage).withStyle(ChatFormatting.GRAY));
        }
        lines.add(Component.empty());
        Optional<MemoryCardData> data = MemoryCardItem.getData(stack);
        if (data.isEmpty()) {
            lines.add(Component.translatable(TOOLTIP + "empty").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
            return;
        }
        lines.add(Component.translatable(TOOLTIP + "source." + data.get().source().name().toLowerCase())
                .withStyle(ChatFormatting.AQUA));
        if (data.get().base() != null) {
            appendBase(data.get().base(), lines);
        }
        appendTrust(data.get().trust(), lines);
        if (data.get().camo() != null) {
            appendCamo(data.get().camo(), lines);
        }
    }

    private static void appendBase(MemoryCardData.BaseSettings base, List<Component> lines) {
        lines.add(Component.empty());
        lines.add(heading("base"));
        lines.add(line("mode", Component.translatable(base.mode().getTranslationKey()).withStyle(ChatFormatting.GREEN)));
        lines.add(line("target", Component.translatable(GUI + (base.multiTargeting() ? "multi" : "single"))
                .withStyle(ChatFormatting.WHITE)));
        lines.add(line("attack_mobs", yesNo(base.targetMobs())));
        lines.add(line("attack_neutrals", yesNo(base.targetPassive())));
        lines.add(line("attack_players", yesNo(base.targetPlayers())));
        lines.add(line("range", white(String.valueOf(base.range()))));
    }

    private static void appendTrust(MemoryCardData.TrustSettings settings, List<Component> lines) {
        lines.add(Component.empty());
        lines.add(heading("trust"));
        List<TrustedPlayer> trusted = settings.trustedPlayers();
        lines.add(line("trusted_players", white(String.valueOf(trusted.size()))));
        for (TrustedPlayer player : trusted.subList(0, Math.min(MAX_LISTED_PLAYERS, trusted.size()))) {
            lines.add(Component.literal("  " + player.getName() + " ").withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(GUI + "access_level." + player.getAccessLevel().getSerializedName())
                            .withStyle(ChatFormatting.DARK_GRAY)));
        }
        if (trusted.size() > MAX_LISTED_PLAYERS) {
            lines.add(Component.translatable(TOOLTIP + "more_players", trusted.size() - MAX_LISTED_PLAYERS)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static void appendCamo(MemoryCardData.CamoSettings camo, List<Component> lines) {
        lines.add(Component.empty());
        lines.add(heading("camo"));
        lines.add(line("disguise", disguiseName(camo.disguise())));
        if (camo.light() != null) {
            lines.add(line("light_value", white(String.valueOf(camo.light().value()))));
            lines.add(line("light_opacity", white(String.valueOf(camo.light().opacity()))));
        }
    }

    /** The block name of the disguise, or "none"; ids of blocks that no longer exist are shown as they are. */
    private static Component disguiseName(String id) {
        if (id == null) return Component.translatable(GUI + "none").withStyle(ChatFormatting.WHITE);
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location != null && BuiltInRegistries.BLOCK.containsKey(location)) {
            return BuiltInRegistries.BLOCK.get(location).getName().withStyle(ChatFormatting.WHITE);
        }
        return white(id);
    }

    private static Component heading(String section) {
        return Component.literal("--").append(Component.translatable(TOOLTIP + section + ".label")).append("--")
                .withStyle(ChatFormatting.YELLOW);
    }

    /** Gold "Label: " followed by the value. */
    private static Component line(String labelKey, Component value) {
        return Component.translatable(GUI + labelKey).withStyle(ChatFormatting.GOLD)
                .append(Component.literal(": ").withStyle(ChatFormatting.GOLD)).append(value);
    }

    private static MutableComponent white(String text) {
        return Component.literal(text).withStyle(ChatFormatting.WHITE);
    }

    private static Component yesNo(boolean value) {
        return Component.translatable(GUI + (value ? "yes" : "no")).withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}

package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.block.base.TurretBaseBlock;
import omtteam.openmodularturrets.config.BaseSetting;
import omtteam.openmodularturrets.config.OMTConfig;

import java.util.List;

/**
 * Turret base item tooltip, ported from oldmod {@code ItemBlockTurretBase.addInformation}. Uses the server's config
 * values when connected to a server.
 */
public final class TurretBaseTooltip {
    private static final String PREFIX = "tooltip.openmodularturrets.";

    private TurretBaseTooltip() {
    }

    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof TurretBaseBlock base)) return;
        if (!Screen.hasShiftDown()) {
            lines.add(shiftHint());
            return;
        }
        int tier = base.getTier();
        BaseSetting settings = OMTConfig.get().bases.tier(tier);

        lines.add(text("turret_base.description"));
        lines.add(Component.empty());
        lines.add(heading("energy.label", ChatFormatting.AQUA));
        lines.add(value("energy.max", settings.baseMaxCharge));
        lines.add(value("energy.io", settings.baseMaxIo));
        lines.add(Component.empty());
        lines.add(heading("extras.label", ChatFormatting.GREEN));
        if (tier == 1) {
            lines.add(text("extras.addons.0"));
        } else {
            lines.add(text("extras.addons.2"));
            lines.add(text(tier == 5 ? "extras.upgrade.2" : "extras.upgrade.1"));
        }
        lines.add(value("turret_limit", settings.baseMaxTurrets));
        lines.add(Component.empty());
        if (tier == 5) {
            lines.add(flavour("flavour.base.5a"));
            lines.add(flavour("flavour.base.5b"));
        } else {
            lines.add(flavour("flavour.base." + tier));
        }
    }

    /** "Press Shift for details." with a highlighted key name (OMLib {@code GeneralUtil.getShiftDetail}). */
    public static Component shiftHint() {
        Component key = Component.translatable(PREFIX + "shift").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC);
        return Component.translatable(PREFIX + "shift_detail", key).withStyle(ChatFormatting.GRAY);
    }

    private static MutableComponent text(String key) {
        return Component.translatable(PREFIX + key).withStyle(ChatFormatting.GRAY);
    }

    private static Component heading(String key, ChatFormatting color) {
        return Component.literal("--").append(Component.translatable(PREFIX + key)).append("--").withStyle(color);
    }

    private static Component value(String key, int value) {
        return text(key).append(": ").append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.WHITE));
    }

    private static Component flavour(String key) {
        return Component.translatable(PREFIX + key).withStyle(ChatFormatting.DARK_GRAY);
    }
}

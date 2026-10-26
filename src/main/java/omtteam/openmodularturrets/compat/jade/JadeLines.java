package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;

/**
 * Lines of the Jade tooltips, free of Jade: the server half of each provider builds them from the data it sent, the
 * client half only shows them.
 */
public final class JadeLines {
    private static final String GUI = "gui.openmodularturrets.";

    private JadeLines() {
    }

    /** "Label: value" line in the colours of the old WAILA tooltips (gold label, white value). */
    static Component line(String labelKey, Component value) {
        return Component.translatable(GUI + labelKey).withStyle(ChatFormatting.GOLD)
                .append(Component.literal(": ").withStyle(ChatFormatting.GOLD)).append(value);
    }

    static Component line(String labelKey, String value) {
        return line(labelKey, Component.literal(value).withStyle(ChatFormatting.WHITE));
    }

    /** The owner line: the owner's name, or "nobody" with a hint to claim the block (green, it is up for grabs). */
    static Component ownerLine(@Nullable String owner) {
        return owner != null ? line("owner", owner)
                : line("owner", Component.translatable(GUI + "unowned").withStyle(ChatFormatting.GREEN));
    }

    /** Green "yes" or red "no". */
    static MutableComponent yesNo(boolean value) {
        return Component.translatable(GUI + (value ? "yes" : "no"))
                .withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }
}

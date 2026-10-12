package omtteam.openmodularturrets.client.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Text helpers shared by the base screens (OMLib {@code GeneralUtil} replacements).
 */
final class ScreenTexts {
    private ScreenTexts() {
    }

    static MutableComponent gui(String key) {
        return Component.translatable("gui.openmodularturrets." + key);
    }

    /** Green "yes" / red "no" (OMLib {@code getColoredBooleanLocalizationYesNo}). */
    static Component yesNo(boolean value) {
        return gui(value ? "yes" : "no").withStyle(value ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    /** "Label: value". */
    static MutableComponent labelled(String key, Component value) {
        return gui(key).append(": ").append(value);
    }
}

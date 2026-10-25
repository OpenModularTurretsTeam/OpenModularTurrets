package omtteam.openmodularturrets.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
//? if >=1.21 {
import snownee.jade.api.IComponentProvider;
//?} else {
/*import snownee.jade.api.IBlockComponentProvider;
*///?}

/**
 * Base of the Jade providers: server data and block tooltip in one class. The tooltip interface is the only thing that
 * differs between the Jade versions of 1.20.1 and 1.21.1.
 */
abstract class JadeBlockProvider implements IServerDataProvider<BlockAccessor>,
        //? if >=1.21 {
        IComponentProvider<BlockAccessor>
        //?} else {
        /*IBlockComponentProvider
        *///?}
{
    private static final String GUI = "gui.openmodularturrets.";

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

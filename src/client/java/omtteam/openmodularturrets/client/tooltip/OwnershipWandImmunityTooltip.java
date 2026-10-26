package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.omlib.item.OwnershipWandItem;

import java.util.List;

/** The mod's own line on the library's ownership wand tooltip: turret shots do not affect the player holding it. */
public final class OwnershipWandImmunityTooltip {
    private OwnershipWandImmunityTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof OwnershipWandItem)) return;
        lines.add(Component.translatable("tooltip.openmodularturrets.ownership_wand.desc4").withStyle(ChatFormatting.GRAY));
    }
}

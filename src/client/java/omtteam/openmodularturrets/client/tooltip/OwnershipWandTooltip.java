package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.item.OwnershipWandItem;

import java.util.List;

/** Ownership wand tooltip: its usage and whether it spreads to connected blocks. */
public final class OwnershipWandTooltip {
    private static final String TOOLTIP = "tooltip.openmodularturrets.ownership_wand.";

    private OwnershipWandTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof OwnershipWandItem)) return;
        for (String usage : List.of("desc1", "desc2", "desc3")) {
            lines.add(Component.translatable(TOOLTIP + usage).withStyle(ChatFormatting.GRAY));
        }
        lines.add(Component.translatable(TOOLTIP + (OwnershipWandItem.isSpreading(stack) ? "spread_on" : "spread_off"))
                .withStyle(ChatFormatting.AQUA));
    }
}

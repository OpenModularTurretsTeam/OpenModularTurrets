package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.omlib.client.compat.ClientCompat;
import omtteam.openmodularturrets.block.misc.InventoryExpanderBlock;

import java.util.List;

/**
 * Inventory expander tooltip: the stacks it adds to each slot (the server's config), worded like the generator's.
 */
public final class InventoryExpanderTooltip {
    private InventoryExpanderTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem item
                && item.getBlock() instanceof InventoryExpanderBlock expander)) return;
        if (!ClientCompat.hasShiftDown()) {
            lines.add(TurretBaseTooltip.shiftHint());
            return;
        }
        lines.add(Component.translatable("tooltip.openmodularturrets.inventory_expander.desc1", expander.setting().stacks)
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.translatable("tooltip.openmodularturrets.inventory_expander.desc2")
                .withStyle(ChatFormatting.GRAY));
    }
}

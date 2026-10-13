package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.List;

/**
 * Crank tooltip (oldmod {@code ItemBlockLever.addInformation}).
 */
public final class LeverTooltip {
    private LeverTooltip() {
    }

    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!stack.is(ModItems.LEVER)) return;
        lines.add(Minecraft.getInstance().hasShiftDown()
                ? Component.translatable("tooltip.openmodularturrets.lever_block").withStyle(ChatFormatting.GRAY)
                : TurretBaseTooltip.shiftHint());
    }
}

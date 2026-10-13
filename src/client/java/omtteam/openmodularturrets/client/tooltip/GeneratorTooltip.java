package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.registry.ModItems;

import java.util.List;

/**
 * Generator tooltip (new block, worded like the crank tooltip).
 */
public final class GeneratorTooltip {
    private GeneratorTooltip() {
    }

    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!stack.is(ModItems.GENERATOR)) return;
        lines.add(Minecraft.getInstance().hasShiftDown()
                ? Component.translatable("tooltip.openmodularturrets.generator").withStyle(ChatFormatting.GRAY)
                : TurretBaseTooltip.shiftHint());
    }
}

package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.omlib.client.compat.ClientCompat;
import omtteam.openmodularturrets.item.AmmoItem;

import java.util.List;

/**
 * Ammo item tooltip, ported from 1.12.2 {@code AmmoMetaItem.addInformation}.
 */
public final class AmmoTooltip {
    private AmmoTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof AmmoItem ammo)) return;
        if (!ClientCompat.hasShiftDown()) {
            lines.add(TurretBaseTooltip.shiftHint());
            return;
        }
        lines.add(Component.empty());
        lines.add(Component.translatable("tooltip.openmodularturrets.ammo.label").withStyle(ChatFormatting.BLUE));
        lines.add(Component.empty());
        lines.add(Component.translatable("tooltip." + ammo.getAmmoType().getNamespace() + ".ammo."
                        + ammo.getAmmoType().getPath())
                .withStyle(ChatFormatting.GRAY));
    }
}

package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.config.ConfigData;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.registry.TurretRegistry;

import java.util.List;
import java.util.Optional;

/**
 * Turret item tooltip: what the turret does, the base tier it needs, its ammo and its energy per shot (with shift).
 * Turrets that ignore scattershot upgrades (the railgun) say so without shift, so it is not missed. Uses the
 * server's config values when connected to a server.
 */
public final class TurretTooltip {
    private static final String PREFIX = "tooltip.openmodularturrets.";

    private TurretTooltip() {
    }

    @SuppressWarnings("unused")
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem item) || !(item.getBlock() instanceof TurretBlock turret)) return;
        TurretType type = turret.getTurretType();
        if (!type.usesScattershot()) {
            lines.add(translated("turret.ignores_scattershot").withStyle(ChatFormatting.GOLD));
        }
        if (!Screen.hasShiftDown()) {
            lines.add(TurretBaseTooltip.shiftHint());
            return;
        }
        ConfigData.Turrets turrets = OMTConfig.get().turrets;
        lines.add(Component.translatable("tooltip." + type.getNamespace() + ".turret." + type.getPath() + ".description")
                .withStyle(ChatFormatting.GRAY));
        lines.add(Component.empty());
        lines.add(value("turret.tier", Component.literal(String.valueOf(type.getRequiredBaseTier()))));
        lines.add(value("turret.ammo", ammo(type, turrets.doTurretsNeedAmmo)));
        lines.add(value("turret.energy", translated("turret.energy.per_shot", turrets.get(type).powerUsage)));
    }

    private static Component ammo(TurretType type, boolean ammoNeeded) {
        Optional<AmmoType> ammo = type.getAmmo();
        if (ammo.isEmpty() || !ammoNeeded) return translated("turret.ammo.none");
        return TurretRegistry.ammo(ammo.get()).map(definition -> definition.displayName().get())
                .orElseGet(() -> Component.literal(ammo.get().getId()));
    }

    /** "Label: value" with a white value. */
    private static Component value(String key, Component value) {
        return translated(key).withStyle(ChatFormatting.GRAY).append(": ")
                .append(value.copy().withStyle(ChatFormatting.WHITE));
    }

    private static MutableComponent translated(String key, Object... args) {
        return Component.translatable(PREFIX + key, args);
    }
}

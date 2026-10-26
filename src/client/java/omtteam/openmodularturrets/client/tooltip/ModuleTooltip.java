package omtteam.openmodularturrets.client.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import omtteam.omlib.client.compat.ClientCompat;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.item.AddonItem;
import omtteam.openmodularturrets.item.GeneratorUpgradeItem;
import omtteam.openmodularturrets.item.TurretBaseUpgradeItem;
import omtteam.openmodularturrets.item.UpgradeItem;

import java.util.List;

/**
 * Upgrade and addon tooltips, ported from 1.12.2 {@code UpgradeMetaItem.addInformation} and
 * {@code AddonMetaItem.addInformation}.
 */
public final class ModuleTooltip {
    private static final String PREFIX = "tooltip.openmodularturrets.";

    private ModuleTooltip() {
    }

    @SuppressWarnings({"unused", "IfCanBeSwitch"})
    public static void append(ItemStack stack, TooltipFlag flag, List<Component> lines) {
        boolean upgrade = stack.getItem() instanceof UpgradeItem;
        boolean addon = stack.getItem() instanceof AddonItem;
        if (!upgrade && !addon) return;
        // which machine an upgrade fits into, also without shift (each machine has its own upgrades)
        if (stack.getItem() instanceof TurretBaseUpgradeItem) {
            lines.add(text("upgrade.machine.turret_base"));
        } else if (stack.getItem() instanceof GeneratorUpgradeItem) {
            lines.add(text("upgrade.machine.generator"));
        }
        if (!ClientCompat.hasShiftDown()) {
            lines.add(TurretBaseTooltip.shiftHint());
            return;
        }
        lines.add(Component.empty());
        if (stack.getItem() instanceof TurretBaseUpgradeItem item) {
            lines.add(translated("upgrade.label").withStyle(ChatFormatting.BLUE));
            lines.add(Component.empty());
            appendUpgrade(item, lines);
        } else if (stack.getItem() instanceof GeneratorUpgradeItem item) {
            lines.add(translated("generator_upgrade.label").withStyle(ChatFormatting.BLUE));
            lines.add(Component.empty());
            appendGeneratorUpgrade(item, lines);
        } else if (stack.getItem() instanceof AddonItem item) {
            lines.add(translated("addon.label").withStyle(ChatFormatting.RED));
            lines.add(Component.empty());
            appendAddon(item, lines);
        }
    }

    private static void appendUpgrade(TurretBaseUpgradeItem item, List<Component> lines) {
        switch (item.getUpgradeType()) {
            case ACCURACY -> {
                effectsAndStacking(lines);
                lines.add(flavour("upgrade.acc.flavour.a"));
                lines.add(flavour("upgrade.acc.flavour.b"));
            }
            case EFFICIENCY -> {
                effectsAndStacking(lines);
                lines.add(flavour("upgrade.eff.flavour"));
            }
            case FIRE_RATE -> {
                effectsAndStacking(lines);
                lines.add(flavour("upgrade.rof.flavour"));
            }
            case RANGE -> {
                effectsAndStacking(lines);
                lines.add(flavour("upgrade.range.flavour"));
            }
            case SCATTERSHOT -> {
                lines.add(text("upgrade.scatter.a"));
                lines.add(text("upgrade.scatter.b"));
                lines.add(translated("upgrade.scatter.ignored").withStyle(ChatFormatting.GOLD));
                lines.add(Component.empty());
                lines.add(text("upgrade.stacks"));
                lines.add(Component.empty());
                lines.add(flavour("upgrade.scatter.flavour"));
            }
            case CREATIVE -> {
                lines.add(text("upgrade.creative.a"));
                lines.add(text("upgrade.creative.b"));
                lines.add(Component.empty());
                lines.add(flavour("upgrade.creative.flavour"));
            }
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static void appendGeneratorUpgrade(GeneratorUpgradeItem item, List<Component> lines) {
        switch (item.getUpgradeType()) {
            case ENERGY_MULTIPLIER -> {
                lines.add(text("upgrade.energy_multiplier.a"));
                lines.add(text("upgrade.energy_multiplier.b"));
                lines.add(text("upgrade.stacks"));
                lines.add(Component.empty());
                lines.add(flavour("upgrade.energy_multiplier.flavour"));
            }
        }
    }

    private static void effectsAndStacking(List<Component> lines) {
        lines.add(text("upgrade.turretinfo"));
        lines.add(text("upgrade.stacks"));
        lines.add(Component.empty());
    }

    private static void appendAddon(AddonItem item, List<Component> lines) {
        switch (item.getAddonType()) {
            case CONCEALER -> {
                lines.add(text("addon.concealer.1"));
                lines.add(Component.empty());
                lines.add(flavour("addon.concealer.flavour"));
            }
            case DAMAGE_AMP -> {
                lines.add(text("upgrade.turretinfo"));
                lines.add(text("addon.amp.a.label"));
                lines.add(text("addon.amp.b.label"));
                lines.add(Component.empty());
                lines.add(flavour("addon.amp.flavour"));
            }
            case RECYCLER -> {
                lines.add(text("addon.recycler.1"));
                lines.add(text("addon.recycler.2"));
                lines.add(Component.empty());
                lines.add(flavour("addon.recycler.flavour"));
            }
            case REDSTONE_REACTOR -> {
                // 1.12.2 showed nothing here although the texts existed
                int generation = OMTConfig.get().miscellaneous.redstoneReactorAddonGen;
                lines.add(text("addon.redstone.a").append(" " + generation + " ").append(translated("addon.redstone.b")));
                lines.add(text("addon.redstone.c"));
                lines.add(Component.empty());
                lines.add(flavour("addon.redstone.flavour"));
            }
            case SOLAR_PANEL -> {
                int generation = OMTConfig.get().miscellaneous.solarPanelAddonGen;
                lines.add(text("addon.solar.a").append(" " + generation + " ").append(translated("addon.solar.b")));
                lines.add(Component.empty());
                lines.add(flavour("addon.solar.flavour"));
            }
            case FAKE_DROPS -> {
                lines.add(text("addon.fakedrops.a"));
                lines.add(text("addon.fakedrops.b"));
                // new loot rule: without this addon turret kills drop nothing, more addons add Looting
                lines.add(text("addon.fakedrops.c"));
                lines.add(text("addon.fakedrops.d"));
                lines.add(Component.empty());
                lines.add(flavour("addon.fakedrops.flavour"));
            }
            case POTENTIA, SERIAL_PORT -> {
            }
        }
    }

    private static MutableComponent translated(String key) {
        return Component.translatable(PREFIX + key);
    }

    private static MutableComponent text(String key) {
        return translated(key).withStyle(ChatFormatting.GRAY);
    }

    private static Component flavour(String key) {
        return translated(key).withStyle(ChatFormatting.DARK_GRAY);
    }
}

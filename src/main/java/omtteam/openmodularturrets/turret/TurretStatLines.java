package omtteam.openmodularturrets.turret;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Values of the range falloff and the turn rate (issue 539) as shown in the turret item tooltip and in Jade, so both
 * show the same text. The results are white values for a "Label: value" line.
 */
public final class TurretStatLines {
    private static final String PREFIX = "tooltip.openmodularturrets.turret.";
    /** Game ticks per second: the turn rate is configured per tick and shown per second. */
    private static final int TICKS_PER_SECOND = 20;

    private TurretStatLines() {
    }

    /**
     * "N blocks (M% damage at R)", or "Full damage up to R blocks" without falloff. Both shares are clamped to 0–1, as
     * the falloff itself does ({@link omtteam.openmodularturrets.core.turret.ProjectileMath#falloff}).
     *
     * @param share       {@code optimalRange}, the share of the range with full damage
     * @param damageAtMax {@code falloffDamage}, the share of the damage at the end of the range
     * @param range       the range the shares apply to, in blocks
     */
    public static Component optimalRange(double share, double damageAtMax, int range) {
        double optimal = clamp(share);
        double minimum = clamp(damageAtMax);
        if (optimal >= 1.0D || minimum >= 1.0D) {
            return Component.translatable(PREFIX + "optimal_range.full", String.valueOf(range))
                    .withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable(PREFIX + "optimal_range.value", format(optimal * range),
                String.valueOf(Math.round(minimum * 100)), String.valueOf(range)).withStyle(ChatFormatting.WHITE);
    }

    /**
     * "N°/s", or "Instant" for a turn rate of 0.
     *
     * @param degreesPerTick {@code turnRate}
     */
    public static Component turnRate(double degreesPerTick) {
        if (degreesPerTick <= 0) {
            return Component.translatable(PREFIX + "turn_rate.instant").withStyle(ChatFormatting.WHITE);
        }
        return Component.translatable(PREFIX + "turn_rate.value", format(degreesPerTick * TICKS_PER_SECOND))
                .withStyle(ChatFormatting.WHITE);
    }

    /** One decimal at most, without a trailing ".0" ({@code 7.5}, {@code 9}). */
    static String format(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private static double clamp(double share) {
        return Math.max(0.0D, Math.min(1.0D, share));
    }
}

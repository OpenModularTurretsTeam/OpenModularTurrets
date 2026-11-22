package omtteam.openmodularturrets.turret;

import omtteam.openmodularturrets.core.turret.TurretType;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Values of the range falloff and the turn rate (issue 539) as shown in the turret item tooltip, in Hwyla and in The
 * One Probe, so all three show the same text (1.20.1, 1.21.1 and 1.21.11 {@code TurretStatLines}). The results are the
 * value part of a "Label: value" line, without formatting codes; the callers show them white.
 * <p>
 * The text is localized by the caller's {@link Localizer}: {@code I18n.format} on the client (tooltip, Hwyla), the
 * server's translation for The One Probe, whose own localization takes no format arguments.
 */
public final class TurretStatLines {
    private static final String PREFIX = "tooltip.openmodularturrets.turret.";
    /** Game ticks per second: the turn rate is configured per tick and shown per second. */
    private static final int TICKS_PER_SECOND = 20;

    /** Translates a key with format arguments, like {@code I18n.format}. */
    @FunctionalInterface
    public interface Localizer {
        String format(String key, Object... args);
    }

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
    public static String optimalRange(double share, double damageAtMax, int range, Localizer localizer) {
        double optimal = clamp(share);
        double minimum = clamp(damageAtMax);
        if (optimal >= 1.0D || minimum >= 1.0D) {
            return localizer.format(PREFIX + "optimal_range.full", String.valueOf(range));
        }
        return localizer.format(PREFIX + "optimal_range.value", format(optimal * range),
                String.valueOf(Math.round(minimum * 100)), String.valueOf(range));
    }

    /**
     * "N°/s", or "Instant" for a turn rate of 0.
     *
     * @param degreesPerTick {@code turnRate}
     */
    public static String turnRate(double degreesPerTick, Localizer localizer) {
        if (degreesPerTick <= 0) {
            return localizer.format(PREFIX + "turn_rate.instant");
        }
        return localizer.format(PREFIX + "turn_rate.value", format(degreesPerTick * TICKS_PER_SECOND));
    }

    /**
     * The effect of a turret with an {@code effectStrength} above 0 (issue 324), in the turret's own words: the
     * translation key {@code tooltip.<namespace>.turret.<path>.effect} with the strength as its argument (e.g.
     * "Push 0.8").
     *
     * @return null for a turret without an effect
     */
    @Nullable
    public static String effect(TurretType type, double effectStrength, Localizer localizer) {
        if (effectStrength <= 0) return null;
        return localizer.format("tooltip." + type.getNamespace() + ".turret." + type.getPath() + ".effect",
                format(effectStrength));
    }

    /** One decimal at most, without a trailing ".0" ({@code 7.5}, {@code 9}). */
    static String format(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    private static double clamp(double share) {
        return Math.max(0.0D, Math.min(1.0D, share));
    }
}

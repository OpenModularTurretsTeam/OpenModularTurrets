package omtteam.openmodularturrets.turret;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/** The tooltip and Jade values of the range falloff and the turn rate, by translation key and arguments. */
class TurretStatLinesTest {
    private static final String PREFIX = "tooltip.openmodularturrets.turret.";

    @Test
    void optimalRangeShowsTheFullDamageDistanceAndTheDamageAtTheEnd() {
        assertTranslation(TurretStatLines.optimalRange(0.6D, 0.5D, 18), "optimal_range.value", "10.8", "50", "18");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 0.3D, 15), "optimal_range.value", "7.5", "30", "15");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 0.5D, 20), "optimal_range.value", "10", "50", "20");
    }

    /** Without falloff (either share at 1, or above it) the whole range deals full damage. */
    @Test
    void optimalRangeWithoutFalloffIsFullDamage() {
        assertTranslation(TurretStatLines.optimalRange(1, 1, 30), "optimal_range.full", "30");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 1, 30), "optimal_range.full", "30");
        assertTranslation(TurretStatLines.optimalRange(1.5D, 0.5D, 30), "optimal_range.full", "30");
    }

    @Test
    void optimalRangeClampsTheShares() {
        assertTranslation(TurretStatLines.optimalRange(-1, -0.5D, 10), "optimal_range.value", "0", "0", "10");
    }

    /** The turn rate is configured per tick and shown per second. */
    @Test
    void turnRateIsShownInDegreesPerSecond() {
        assertTranslation(TurretStatLines.turnRate(10), "turn_rate.value", "200");
        assertTranslation(TurretStatLines.turnRate(0.25D), "turn_rate.value", "5");
        assertTranslation(TurretStatLines.turnRate(0.33D), "turn_rate.value", "6.6");
        assertTranslation(TurretStatLines.turnRate(0), "turn_rate.instant");
    }

    private static void assertTranslation(Component component, String key, Object... args) {
        TranslatableContents contents = assertInstanceOf(TranslatableContents.class, component.getContents());
        assertEquals(PREFIX + key, contents.getKey());
        assertArrayEquals(args, contents.getArgs());
    }
}

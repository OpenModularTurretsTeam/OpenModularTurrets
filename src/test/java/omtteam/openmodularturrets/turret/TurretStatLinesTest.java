package omtteam.openmodularturrets.turret;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The tooltip, Hwyla and The One Probe values of the range falloff and the turn rate, by translation key and
 * arguments (the same keys and arguments as on 1.20.1, 1.21.1 and 1.21.11).
 */
class TurretStatLinesTest {
    private static final String PREFIX = "tooltip.openmodularturrets.turret.";
    /** Writes the key and its arguments instead of translating them. */
    private static final TurretStatLines.Localizer KEYS = (key, args) -> key + Arrays.toString(args);

    @Test
    void optimalRangeShowsTheFullDamageDistanceAndTheDamageAtTheEnd() {
        assertTranslation(TurretStatLines.optimalRange(0.6D, 0.5D, 18, KEYS), "optimal_range.value", "10.8", "50", "18");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 0.3D, 15, KEYS), "optimal_range.value", "7.5", "30", "15");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 0.5D, 20, KEYS), "optimal_range.value", "10", "50", "20");
    }

    /** Without falloff (either share at 1, or above it) the whole range deals full damage. */
    @Test
    void optimalRangeWithoutFalloffIsFullDamage() {
        assertTranslation(TurretStatLines.optimalRange(1, 1, 30, KEYS), "optimal_range.full", "30");
        assertTranslation(TurretStatLines.optimalRange(0.5D, 1, 30, KEYS), "optimal_range.full", "30");
        assertTranslation(TurretStatLines.optimalRange(1.5D, 0.5D, 30, KEYS), "optimal_range.full", "30");
    }

    @Test
    void optimalRangeClampsTheShares() {
        assertTranslation(TurretStatLines.optimalRange(-1, -0.5D, 10, KEYS), "optimal_range.value", "0", "0", "10");
    }

    /** The turn rate is configured per tick and shown per second. */
    @Test
    void turnRateIsShownInDegreesPerSecond() {
        assertTranslation(TurretStatLines.turnRate(10, KEYS), "turn_rate.value", "200");
        assertTranslation(TurretStatLines.turnRate(0.25D, KEYS), "turn_rate.value", "5");
        assertTranslation(TurretStatLines.turnRate(0.33D, KEYS), "turn_rate.value", "6.6");
        assertTranslation(TurretStatLines.turnRate(0, KEYS), "turn_rate.instant");
    }

    /** With the lang files' texts the values read as on 1.21.11 ({@code %%} is a percent sign there). */
    @Test
    void englishAndGermanTexts() throws IOException {
        TurretStatLines.Localizer english = localizer("en_US");
        assertEquals("10.8 blocks (50% damage at 18)", TurretStatLines.optimalRange(0.6D, 0.5D, 18, english));
        assertEquals("Full damage up to 30 blocks", TurretStatLines.optimalRange(1, 1, 30, english));
        assertEquals("200°/s", TurretStatLines.turnRate(10, english));
        assertEquals("Instant", TurretStatLines.turnRate(0, english));
        TurretStatLines.Localizer german = localizer("de_DE");
        assertEquals("10.8 Blöcke (50% Schaden bei 18)", TurretStatLines.optimalRange(0.6D, 0.5D, 18, german));
        assertEquals("Voller Schaden bis 30 Blöcke", TurretStatLines.optimalRange(1, 1, 30, german));
        assertEquals("200°/s", TurretStatLines.turnRate(10, german));
        assertEquals("Sofort", TurretStatLines.turnRate(0, german));
    }

    /** {@code String.format} over a lang file of the mod, as {@code I18n.format} does. */
    private static TurretStatLines.Localizer localizer(String language) throws IOException {
        Map<String, String> texts = new HashMap<>();
        String path = "/assets/openmodularturrets/lang/" + language + ".lang";
        try (InputStream in = TurretStatLinesTest.class.getResourceAsStream(path)) {
            assertNotNull(in, path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                int split = line.indexOf('=');
                if (!line.startsWith("#") && split > 0) texts.put(line.substring(0, split), line.substring(split + 1));
            }
        }
        return (key, args) -> {
            String text = texts.get(key);
            assertNotNull(text, key + " is missing in " + language);
            return String.format(text, args);
        };
    }

    private static void assertTranslation(String text, String key, Object... args) {
        assertEquals(PREFIX + key + Arrays.toString(args), text);
    }
}

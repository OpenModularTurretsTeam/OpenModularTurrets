package omtteam.openmodularturrets.core.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TargetingSettingsTest {
    @Test
    void constructorClampsNegativeValues() {
        TargetingSettings settings = new TargetingSettings(false, true, false, -3, -1);
        assertEquals(0, settings.getMaxRange());
        assertEquals(0, settings.getRange());
    }

    @Test
    void constructorLimitsTheRangeToTheMaximum() {
        assertEquals(10, new TargetingSettings(false, true, false, 25, 10).getRange());
    }

    @Test
    void negativeMaxRangeCountsAsZero() {
        TargetingSettings settings = new TargetingSettings(false, true, false, 5, 10);
        settings.setMaxRange(-1);
        assertEquals(0, settings.getMaxRange());
        assertEquals(0, settings.getRange(), "the range is never negative");
    }

    @Test
    void loweringTheMaxRangeLowersTheRange() {
        TargetingSettings settings = new TargetingSettings(false, true, false, 8, 10);
        settings.setMaxRange(6);
        assertEquals(6, settings.getRange());
        settings.setMaxRange(20);
        assertEquals(6, settings.getRange(), "raising the maximum keeps the range");
    }

    @Test
    void rangeOutsideTheLimitsIsIgnored() {
        TargetingSettings settings = new TargetingSettings(false, true, false, 5, 10);
        settings.setRange(11);
        settings.setRange(-1);
        assertEquals(5, settings.getRange());
        settings.setRange(10);
        assertEquals(10, settings.getRange());
    }
}

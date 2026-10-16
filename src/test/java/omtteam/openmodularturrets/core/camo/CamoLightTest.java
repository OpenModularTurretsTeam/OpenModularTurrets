package omtteam.openmodularturrets.core.camo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CamoLightTest {
    @Test
    void valuesAreClampedToTheVanillaRange() {
        CamoLight light = new CamoLight(-3, 99);
        assertEquals(0, light.value());
        assertEquals(CamoLight.MAX, light.opacity());
    }

    @Test
    void defaultIsDarkAndOpaque() {
        assertEquals(0, CamoLight.DEFAULT.value());
        assertEquals(CamoLight.MAX, CamoLight.DEFAULT.opacity());
    }

    @Test
    void withersKeepTheOtherValueAndClamp() {
        CamoLight light = new CamoLight(4, 7);
        assertEquals(new CamoLight(9, 7), light.withValue(9));
        assertEquals(new CamoLight(4, 15), light.withOpacity(40));
    }

    @Test
    void onlyTierFourAndFiveBasesHaveAdjustableLight() {
        assertFalse(CamoLight.supportedByBaseTier(1));
        assertFalse(CamoLight.supportedByBaseTier(3));
        assertTrue(CamoLight.supportedByBaseTier(4));
        assertTrue(CamoLight.supportedByBaseTier(5));
    }
}

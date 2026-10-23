package omtteam.openmodularturrets.core.item;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTypesTest {
    /** 1.12.2 {@code AddonSlot.getItemStackLimit}. */
    @Test
    void damageAmpAndFakeDropsStackToFourOtherAddonsToOne() {
        for (AddonType type : AddonType.values()) {
            int expected = type == AddonType.DAMAGE_AMP || type == AddonType.FAKE_DROPS ? 4 : 1;
            assertEquals(expected, type.getMaxPerSlot(), type.name());
        }
    }

    @Test
    void upgradesStackToFour() {
        assertEquals(4, TurretBaseUpgradeType.RANGE.maxPerSlot());
        assertEquals(4, GeneratorUpgradeType.ENERGY_MULTIPLIER.maxPerSlot());
    }

    /** The names are part of the 3.x item ids and translation keys. */
    @Test
    void serializedNamesKeepTheOldIds() {
        assertEquals("damage_amp", AddonType.DAMAGE_AMP.getSerializedName());
        assertEquals("scatter_shot", TurretBaseUpgradeType.SCATTERSHOT.getSerializedName());
        assertEquals("energy_multiplier", GeneratorUpgradeType.ENERGY_MULTIPLIER.getSerializedName());
    }
}

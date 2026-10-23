package omtteam.openmodularturrets.core.item;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The serialized names are part of the item ids and translation keys, so changing one breaks existing worlds.
 */
class ItemTypesTest {
    @Test
    void addonNamesAndStackLimits() {
        assertEquals(List.of("concealer", "damage_amp", "potentia", "recycler", "redstone_reactor", "serial_port",
                        "solar_panel", "fake_drops"),
                Arrays.stream(AddonType.values()).map(AddonType::getSerializedName).toList());
        for (AddonType type : AddonType.values()) {
            int expected = type == AddonType.DAMAGE_AMP || type == AddonType.FAKE_DROPS ? 4 : 1;
            assertEquals(expected, type.getMaxPerSlot(), type.name());
        }
    }

    @Test
    void turretBaseUpgradeNames() {
        assertEquals(List.of("accuracy", "efficiency", "fire_rate", "range", "scatter_shot", "creative"),
                Arrays.stream(TurretBaseUpgradeType.values()).map(UpgradeType::getSerializedName).toList());
    }

    @Test
    void generatorUpgradeNames() {
        assertEquals(List.of("energy_multiplier"),
                Arrays.stream(GeneratorUpgradeType.values()).map(UpgradeType::getSerializedName).toList());
    }

    @Test
    void upgradesStackToFour() {
        for (TurretBaseUpgradeType type : TurretBaseUpgradeType.values()) {
            assertEquals(4, type.maxPerSlot(), type.name());
        }
        assertEquals(4, GeneratorUpgradeType.ENERGY_MULTIPLIER.maxPerSlot());
    }
}

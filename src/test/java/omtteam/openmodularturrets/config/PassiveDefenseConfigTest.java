package omtteam.openmodularturrets.config;

import omtteam.openmodularturrets.block.defense.DefenseTier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PassiveDefenseConfigTest {
    /**
     * The defaults are the strengths the blocks are registered with (the 3.x values), which the mining progress is
     * scaled from ({@code PassiveDefense.destroyProgress}).
     */
    @Test
    void defaultHardnessAndResistanceAreTheRegisteredStrengths() {
        ConfigData.PassiveDefense defaults = new ConfigData.PassiveDefense();
        for (DefenseTier tier : DefenseTier.values()) {
            DefenseSetting setting = defaults.tier(tier.getNumber());
            assertEquals(tier.getStrength(), setting.hardness, 1.0E-6, "hardness of tier " + tier.getNumber());
            assertEquals(tier.getStrength(), setting.explosionResistance, 1.0E-6,
                    "resistance of tier " + tier.getNumber());
        }
    }

    /** 3.x hit for 5 on every tier; tier 1 keeps that and every tier above hits for one more. */
    @Test
    void defaultFenceDamageGrowsWithTheTier() {
        ConfigData.PassiveDefense defaults = new ConfigData.PassiveDefense();
        for (int tier = 1; tier <= 5; tier++) {
            assertEquals(4.0D + tier, defaults.tier(tier).fenceDamage, 1.0E-6, "fence damage of tier " + tier);
        }
    }
}

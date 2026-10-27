package omtteam.openmodularturrets.config;

import omtteam.omlib.config.Comment;
import omtteam.omlib.config.Range;

/**
 * Per-tier turret base settings. Fields and defaults ported from 1.12.2 {@code handler/config/BaseSetting};
 * {@link #baseAmmoStacks} is new (two stacks on tiers 4 and 5).
 */
public class BaseSetting {
    @Comment("Energy capacity of the base.") @Range(min = 1)
    public int baseMaxCharge;
    @Comment("Maximum energy the base accepts per tick.") @Range(min = 1)
    public int baseMaxIo;
    @Comment("Number of turrets that can be attached to the base.") @Range(min = 1)
    public int baseMaxTurrets;
    @Comment("Stacks each ammo slot of the base holds; it multiplies the slot size with attached inventory expanders too.")
    @Range(min = 1, max = 1000)
    public int baseAmmoStacks;
    @Comment("Blast resistance (only used if bases.baseBreakable is true).") @Range(min = 1)
    public int baseBlastResistance;
    @Comment("Hardness (only used if bases.baseBreakable is true).") @Range(min = 1)
    public int baseHardness;

    @SuppressWarnings("unused")
    public BaseSetting() {
    }

    public BaseSetting(int baseMaxCharge, int baseMaxIo, int baseMaxTurrets, int baseAmmoStacks, int baseBlastResistance,
                       int baseHardness) {
        this.baseMaxCharge = baseMaxCharge;
        this.baseMaxIo = baseMaxIo;
        this.baseMaxTurrets = baseMaxTurrets;
        this.baseAmmoStacks = baseAmmoStacks;
        this.baseBlastResistance = baseBlastResistance;
        this.baseHardness = baseHardness;
    }
}

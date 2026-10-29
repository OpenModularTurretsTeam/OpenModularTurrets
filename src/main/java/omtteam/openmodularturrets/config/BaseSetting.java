package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Config;

/**
 * Per-tier turret base settings; {@link #baseAmmoStacks} is new in 4.x (two stacks on tiers 4 and 5, as on 1.20.1+).
 * Bases read charge, input and turret count while the game runs, so only hardness and blast resistance are marked as
 * needing a restart, as on 1.20.1+.
 */
public class BaseSetting {
    @Config.RangeInt(min = 1)
    public int baseMaxCharge;
    @Config.RangeInt(min = 1)
    public int baseMaxIo;
    @Config.RangeInt(min = 1)
    public int baseMaxTurrets;
    @Config.Comment("Stacks each ammo slot of the base holds; it multiplies the slot size with attached inventory expanders too.")
    @Config.RangeInt(min = 1, max = 1000)
    public int baseAmmoStacks;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseBlastResistance;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseHardness;

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
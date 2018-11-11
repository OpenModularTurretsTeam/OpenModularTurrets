package omtteam.openmodularturrets.handler.config;

import net.minecraftforge.common.config.Config;

public class BaseSetting {
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseMaxCharge;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseMaxIo;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseMaxTurrets;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseBlastResistance;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    public int baseHardness;

    public BaseSetting(int baseMaxCharge, int baseMaxIo, int baseMaxTurrets, int baseBlastResistance, int baseHardness) {
        this.baseMaxCharge = baseMaxCharge;
        this.baseMaxIo = baseMaxIo;
        this.baseMaxTurrets = baseMaxTurrets;
        this.baseBlastResistance = baseBlastResistance;
        this.baseHardness = baseHardness;
    }
}
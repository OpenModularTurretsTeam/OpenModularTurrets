package omtteam.openmodularturrets.handler.config;

import net.minecraftforge.common.config.Config;

public class BaseSetting {
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    private int baseMaxCharge;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    private int baseMaxIo;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    private int baseMaxTurrets;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    private int baseBlastResistance;
    @Config.RangeInt(min = 1)
    @Config.RequiresMcRestart
    private int baseHardness;

    public BaseSetting(int baseMaxCharge, int baseMaxIo, int baseMaxTurrets, int baseBlastResistance, int baseHardness) {
        this.baseMaxCharge = baseMaxCharge;
        this.baseMaxIo = baseMaxIo;
        this.baseMaxTurrets = baseMaxTurrets;
        this.baseBlastResistance = baseBlastResistance;
        this.baseHardness = baseHardness;
    }

    public int getBaseMaxCharge() {
        return baseMaxCharge;
    }

    public int getBaseMaxIo() {
        return baseMaxIo;
    }

    public int getBaseMaxTurrets() {
        return baseMaxTurrets;
    }

    public int getBaseBlastResistance() {
        return baseBlastResistance;
    }

    public int getBaseHardness() {
        return baseHardness;
    }
}
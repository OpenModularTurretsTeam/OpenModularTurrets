package omtteam.openmodularturrets.handler.config;

import net.minecraftforge.common.config.Config;

public class TurretSetting {
    @Config.RequiresMcRestart
    public boolean enabled;
    @Config.RangeInt(min = 1, max = 100)
    public int baseRange;
    @Config.RangeInt(min = 1, max = 20)
    public int baseFireRate;
    @Config.RangeInt(min = 0)
    public int baseDamage;
    @Config.RangeInt(min = 0)
    public int powerUsage;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double baseAccuracyDeviation;
    @Config.RangeInt(min = 1, max = 6)
    public int maxSimultaneous;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double damageAmp;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double fireRateUpgrade;
    @Config.RangeInt(min = 0, max = 10)
    public int rangeUpgrade;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double accuracyUpgrade;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double efficiencyUpgrade;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double recyclerNegateChance;
    @Config.RangeDouble(min = 0D, max = 1D)
    public double recyclerAddChance;

    public TurretSetting(boolean enabled, int baseRange, int baseFireRate, int baseDamage, int powerUsage, double baseAccuracyDeviation, int maxSimultaneous, double damageAmp, double fireRateUpgrade, int rangeUpgrade, double accuracyUpgrade, double efficiencyUpgrade, double recyclerNegateChance, double recyclerAddChance) {
        this.enabled = enabled;
        this.baseRange = baseRange;
        this.baseFireRate = baseFireRate;
        this.baseDamage = baseDamage;
        this.powerUsage = powerUsage;
        this.baseAccuracyDeviation = baseAccuracyDeviation;
        this.maxSimultaneous = maxSimultaneous;
        this.damageAmp = damageAmp;
        this.fireRateUpgrade = fireRateUpgrade;
        this.rangeUpgrade = rangeUpgrade;
        this.accuracyUpgrade = accuracyUpgrade;
        this.efficiencyUpgrade = efficiencyUpgrade;
        this.recyclerNegateChance = recyclerNegateChance;
        this.recyclerAddChance = recyclerAddChance;
    }
}
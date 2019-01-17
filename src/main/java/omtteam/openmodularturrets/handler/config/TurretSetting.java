package omtteam.openmodularturrets.handler.config;

import net.minecraftforge.common.config.Config;

public class TurretSetting {
    @Config.RequiresMcRestart
    public boolean enabled;
    @Config.RangeInt(min = 1)
    private int baseRange;
    @Config.RangeInt(min = 1)
    private int baseFireRate;
    @Config.RangeInt(min = 0)
    private int baseDamage;
    @Config.RangeInt(min = 1)
    private int powerUsage;
    @Config.RangeDouble(min = 0D)
    private double baseAccuracyDeviation;
    @Config.RangeInt(min = 1)
    private int maxSimultaneous;
    @Config.RangeDouble(min = 0D)
    private double damageAmp;
    @Config.RangeDouble(min = 0D)
    private double fireRateUpgrade;
    @Config.RangeInt(min = 0)
    private int rangeUpgrade;
    @Config.RangeDouble(min = 0D)
    private double accuracyUpgrade;
    @Config.RangeDouble(min = 0D)
    private double efficiencyUpgrade;
    @Config.RangeDouble(min = 0D)
    private double recyclerNegateChance;
    @Config.RangeDouble(min = 0D)
    private double recyclerAddChance;

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

    public boolean isEnabled() {
        return enabled;
    }

    public int getBaseRange() {
        return baseRange;
    }

    public int getBaseFireRate() {
        return baseFireRate;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getPowerUsage() {
        return powerUsage;
    }

    public double getBaseAccuracyDeviation() {
        return baseAccuracyDeviation;
    }

    public int getMaxSimultaneous() {
        return maxSimultaneous;
    }

    public double getDamageAmp() {
        return damageAmp;
    }

    public double getFireRateUpgrade() {
        return fireRateUpgrade;
    }

    public int getRangeUpgrade() {
        return rangeUpgrade;
    }

    public double getAccuracyUpgrade() {
        return accuracyUpgrade;
    }

    public double getEfficiencyUpgrade() {
        return efficiencyUpgrade;
    }

    public double getRecyclerNegateChance() {
        return recyclerNegateChance;
    }

    public double getRecyclerAddChance() {
        return recyclerAddChance;
    }
}
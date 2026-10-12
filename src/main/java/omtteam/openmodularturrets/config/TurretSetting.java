package omtteam.openmodularturrets.config;

/**
 * Per-turret-type settings. Fields and defaults ported from oldmod {@code handler/config/TurretSetting}.
 */
public class TurretSetting {
    public boolean enabled;
    public int baseRange;
    /** Ticks between shots (shots per second = 20 / baseFireRate). */
    public int baseFireRate;
    public int baseDamage;
    public int powerUsage;
    /** Inaccuracy in percent; displayed accuracy is {@code 100 - deviation}. */
    public double baseAccuracyDeviation;
    /** Maximum number of turrets of this type per base. */
    public int maxSimultaneous;
    public double damageAmp;
    public double fireRateUpgrade;
    public int rangeUpgrade;
    public double accuracyUpgrade;
    public double efficiencyUpgrade;
    public double recyclerNegateChance;
    public double recyclerAddChance;

    public TurretSetting() {
    }

    public TurretSetting(boolean enabled, int baseRange, int baseFireRate, int baseDamage, int powerUsage,
                         double baseAccuracyDeviation, int maxSimultaneous, double damageAmp, double fireRateUpgrade,
                         int rangeUpgrade, double accuracyUpgrade, double efficiencyUpgrade,
                         double recyclerNegateChance, double recyclerAddChance) {
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

    /**
     * Ranges from the oldmod {@code @Config.Range*} annotations, except {@code baseFireRate} (old max 20, but most
     * defaults exceed it) and {@code baseAccuracyDeviation} (old max 1, but it is used as a percentage).
     */
    void validate(ConfigValidator v, String path) {
        baseRange = v.range(path + ".baseRange", baseRange, 1, 100);
        baseFireRate = v.min(path + ".baseFireRate", baseFireRate, 1);
        baseDamage = v.min(path + ".baseDamage", baseDamage, 0);
        powerUsage = v.min(path + ".powerUsage", powerUsage, 0);
        baseAccuracyDeviation = v.range(path + ".baseAccuracyDeviation", baseAccuracyDeviation, 0D, 100D);
        maxSimultaneous = v.range(path + ".maxSimultaneous", maxSimultaneous, 1, 6);
        damageAmp = v.range(path + ".damageAmp", damageAmp, 0D, 1D);
        fireRateUpgrade = v.range(path + ".fireRateUpgrade", fireRateUpgrade, 0D, 1D);
        rangeUpgrade = v.range(path + ".rangeUpgrade", rangeUpgrade, 0, 10);
        accuracyUpgrade = v.range(path + ".accuracyUpgrade", accuracyUpgrade, 0D, 1D);
        efficiencyUpgrade = v.range(path + ".efficiencyUpgrade", efficiencyUpgrade, 0D, 1D);
        recyclerNegateChance = v.range(path + ".recyclerNegateChance", recyclerNegateChance, 0D, 1D);
        recyclerAddChance = v.range(path + ".recyclerAddChance", recyclerAddChance, 0D, 1D);
    }
}

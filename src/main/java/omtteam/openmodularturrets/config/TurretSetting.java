package omtteam.openmodularturrets.config;

import omtteam.openmodularturrets.core.settings.TurretStats;

/**
 * Per-turret-type settings. Fields and defaults ported from 1.12.2 {@code handler/config/TurretSetting}.
 */
public class TurretSetting {
    @Comment("If this turret type can be crafted and placed.")
    public boolean enabled;
    @Comment("Base range in blocks, before range upgrades.") @Range(min = 1, max = 100)
    public int baseRange;
    @Comment("Ticks between shots (shots per second = 20 / value). Lower is faster.") @Range(min = 1)
    public int baseFireRate;
    @Comment("Damage per hit before the damage amp addon.") @Range(min = 0)
    public int baseDamage;
    @Comment("Energy used per shot.") @Range(min = 0)
    public int powerUsage;
    @Comment("Inaccuracy in percent (accuracy = 100 - value). 0 is perfectly accurate.") @Range(min = 0, max = 100)
    public double baseAccuracyDeviation;
    @Comment("Maximum number of turrets of this type per base.") @Range(min = 1, max = 6)
    public int maxSimultaneous;
    @Comment("Share of the target's health added as damage per damage amp addon level.") @Range(min = 0, max = 1)
    public double damageAmp;
    @Comment("Fire rate bonus per fire rate upgrade.") @Range(min = 0, max = 1)
    public double fireRateUpgrade;
    @Comment("Range in blocks added per range upgrade.") @Range(min = 0, max = 10)
    public int rangeUpgrade;
    @Comment("Accuracy bonus per accuracy upgrade.") @Range(min = 0, max = 1)
    public double accuracyUpgrade;
    @Comment("Energy saving per efficiency upgrade.") @Range(min = 0, max = 1)
    public double efficiencyUpgrade;
    @Comment("Chance that the recycler addon saves the ammo of a shot.") @Range(min = 0, max = 1)
    public double recyclerNegateChance;
    @Comment("Chance that the recycler addon returns extra ammo.") @Range(min = 0, max = 1)
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

    public static TurretSetting of(TurretStats s) {
        return new TurretSetting(s.enabled(), s.baseRange(), s.baseFireRate(), s.baseDamage(), s.powerUsage(),
                s.baseAccuracyDeviation(), s.maxSimultaneous(), s.damageAmp(), s.fireRateUpgrade(), s.rangeUpgrade(),
                s.accuracyUpgrade(), s.efficiencyUpgrade(), s.recyclerNegateChance(), s.recyclerAddChance());
    }

    public TurretStats toStats() {
        return new TurretStats(enabled, baseRange, baseFireRate, baseDamage, powerUsage, baseAccuracyDeviation,
                maxSimultaneous, damageAmp, fireRateUpgrade, rangeUpgrade, accuracyUpgrade, efficiencyUpgrade,
                recyclerNegateChance, recyclerAddChance);
    }
}

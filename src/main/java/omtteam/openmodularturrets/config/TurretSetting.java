package omtteam.openmodularturrets.config;

import net.minecraftforge.common.config.Config;
import omtteam.openmodularturrets.core.settings.TurretStats;

public class TurretSetting {
    @Config.RequiresMcRestart
    public boolean enabled;
    @Config.RangeInt(min = 1, max = 100)
    public int baseRange;
    /** Ticks between shots. */
    @Config.RangeInt(min = 1)
    public int baseFireRate;
    @Config.RangeInt(min = 0)
    public int baseDamage;
    @Config.RangeInt(min = 0)
    public int powerUsage;
    /** In percent, like the addon turrets' {@code baseAccuracyDeviation}. */
    @Config.RangeDouble(min = 0D, max = 100D)
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
    @Config.Comment("Share of the range (with range upgrades) up to which the turret deals full damage; from there the damage falls linearly to falloffDamage at the end of the range. 1 = no falloff.")
    @Config.RangeDouble(min = 0D, max = 1D)
    public double optimalRange = TurretStats.NO_FALLOFF_OPTIMAL_RANGE;
    @Config.Comment("Share of the damage dealt at the end of the range and beyond. 1 = no falloff.")
    @Config.RangeDouble(min = 0D, max = 1D)
    public double falloffDamage = TurretStats.NO_FALLOFF_DAMAGE;
    @Config.Comment("Degrees the turret turns per tick towards its target, on each axis. 0 turns instantly.")
    @Config.RangeDouble(min = 0D, max = 180D)
    public double turnRate = TurretStats.INSTANT_TURN;

    @Config.Comment("Strength of the turret's effect: push or pull speed in blocks per tick (repelling and tractor turrets), energy destroyed per shot (energy drain turret). Not used by other turrets.")
    @Config.RangeDouble(min = 0D)
    public double effectStrength = TurretStats.NO_EFFECT;

    /**
     * For Gson (OMLib's config sync of the addon turret settings): without a no-arg constructor Gson skips the field
     * initializers, so a key the server does not send (an older server) would become 0 instead of its default.
     */
    @SuppressWarnings("unused")
    private TurretSetting() {
    }

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

    /**
     * Sets the range falloff and the turn rate (issue 539); without this call a setting has no falloff and turns
     * instantly.
     *
     * @return this setting
     */
    public TurretSetting realism(double optimalRange, double falloffDamage, double turnRate) {
        this.optimalRange = optimalRange;
        this.falloffDamage = falloffDamage;
        this.turnRate = turnRate;
        return this;
    }

    /**
     * Sets the effect strength of an effect turret (issue 324); without this call a setting has none.
     *
     * @return this setting
     */
    public TurretSetting effect(double effectStrength) {
        this.effectStrength = effectStrength;
        return this;
    }
}

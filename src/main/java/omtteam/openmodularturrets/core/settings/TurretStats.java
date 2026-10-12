package omtteam.openmodularturrets.core.settings;

/**
 * Configurable stats of a turret type (oldmod {@code TurretSetting}).
 *
 * @param baseFireRate          ticks between shots
 * @param baseAccuracyDeviation inaccuracy in percent
 * @param maxSimultaneous       maximum number of turrets of this type per base
 * @param rangeUpgrade          range added per range upgrade
 */
public record TurretStats(boolean enabled, int baseRange, int baseFireRate, int baseDamage, int powerUsage,
                          double baseAccuracyDeviation, int maxSimultaneous, double damageAmp,
                          double fireRateUpgrade, int rangeUpgrade, double accuracyUpgrade, double efficiencyUpgrade,
                          double recyclerNegateChance, double recyclerAddChance) {
}

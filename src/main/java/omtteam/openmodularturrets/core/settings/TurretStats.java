package omtteam.openmodularturrets.core.settings;

/**
 * Configurable stats of a turret type (1.12.2 {@code TurretSetting}).
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

    /**
     * Stats with the given range, fire rate, damage and energy per shot; the other values start at those most
     * built-in turrets use and can be changed on the builder.
     */
    public static Builder builder(int baseRange, int baseFireRate, int baseDamage, int powerUsage) {
        return new Builder(baseRange, baseFireRate, baseDamage, powerUsage);
    }

    /** Builds {@link TurretStats}, e.g. the default stats of a turret added through the turret API. */
    public static final class Builder {
        private final int baseRange;
        private final int baseFireRate;
        private final int baseDamage;
        private final int powerUsage;
        private boolean enabled = true;
        private double baseAccuracyDeviation = 10;
        private int maxSimultaneous = 4;
        private double damageAmp = 0.05D;
        private double fireRateUpgrade = 0.1D;
        private int rangeUpgrade = 2;
        private double accuracyUpgrade = 0.2D;
        private double efficiencyUpgrade = 0.08D;
        private double recyclerNegateChance = 0.1D;
        private double recyclerAddChance = 0.05D;

        private Builder(int baseRange, int baseFireRate, int baseDamage, int powerUsage) {
            this.baseRange = baseRange;
            this.baseFireRate = baseFireRate;
            this.baseDamage = baseDamage;
            this.powerUsage = powerUsage;
        }

        /** Default true. */
        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /** Inaccuracy in percent, default 10. */
        public Builder accuracyDeviation(double percent) {
            this.baseAccuracyDeviation = percent;
            return this;
        }

        /** Default 4. */
        public Builder maxSimultaneous(int max) {
            this.maxSimultaneous = max;
            return this;
        }

        /** Default 0.05. */
        public Builder damageAmp(double damageAmp) {
            this.damageAmp = damageAmp;
            return this;
        }

        /** Default 0.1. */
        public Builder fireRateUpgrade(double bonus) {
            this.fireRateUpgrade = bonus;
            return this;
        }

        /** Default 2. */
        public Builder rangeUpgrade(int blocks) {
            this.rangeUpgrade = blocks;
            return this;
        }

        /** Default 0.2. */
        public Builder accuracyUpgrade(double bonus) {
            this.accuracyUpgrade = bonus;
            return this;
        }

        /** Default 0.08. */
        public Builder efficiencyUpgrade(double saving) {
            this.efficiencyUpgrade = saving;
            return this;
        }

        /** Defaults 0.1 and 0.05. */
        public Builder recycler(double negateChance, double addChance) {
            this.recyclerNegateChance = negateChance;
            this.recyclerAddChance = addChance;
            return this;
        }

        /** Creates the stats; values that were not set keep their defaults. */
        public TurretStats build() {
            return new TurretStats(enabled, baseRange, baseFireRate, baseDamage, powerUsage, baseAccuracyDeviation,
                    maxSimultaneous, damageAmp, fireRateUpgrade, rangeUpgrade, accuracyUpgrade, efficiencyUpgrade,
                    recyclerNegateChance, recyclerAddChance);
        }
    }
}

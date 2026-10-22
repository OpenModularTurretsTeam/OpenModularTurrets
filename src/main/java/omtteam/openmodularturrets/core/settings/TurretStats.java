package omtteam.openmodularturrets.core.settings;

import java.util.Objects;

/**
 * Configurable stats of a turret type (1.12.2 {@code TurretSetting}). A record in 1.20.1+.
 */
public final class TurretStats {
    private final boolean enabled;
    private final int baseRange;
    private final int baseFireRate;
    private final int baseDamage;
    private final int powerUsage;
    private final double baseAccuracyDeviation;
    private final int maxSimultaneous;
    private final double damageAmp;
    private final double fireRateUpgrade;
    private final int rangeUpgrade;
    private final double accuracyUpgrade;
    private final double efficiencyUpgrade;
    private final double recyclerNegateChance;
    private final double recyclerAddChance;

    public TurretStats(boolean enabled, int baseRange, int baseFireRate, int baseDamage, int powerUsage,
                       double baseAccuracyDeviation, int maxSimultaneous, double damageAmp, double fireRateUpgrade,
                       int rangeUpgrade, double accuracyUpgrade, double efficiencyUpgrade, double recyclerNegateChance,
                       double recyclerAddChance) {
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

    public boolean enabled() {
        return enabled;
    }

    public int baseRange() {
        return baseRange;
    }

    /** Ticks between shots. */
    public int baseFireRate() {
        return baseFireRate;
    }

    public int baseDamage() {
        return baseDamage;
    }

    public int powerUsage() {
        return powerUsage;
    }

    /** Inaccuracy in percent. */
    public double baseAccuracyDeviation() {
        return baseAccuracyDeviation;
    }

    /** Maximum number of turrets of this type per base. */
    public int maxSimultaneous() {
        return maxSimultaneous;
    }

    public double damageAmp() {
        return damageAmp;
    }

    public double fireRateUpgrade() {
        return fireRateUpgrade;
    }

    /** Range added per range upgrade. */
    public int rangeUpgrade() {
        return rangeUpgrade;
    }

    public double accuracyUpgrade() {
        return accuracyUpgrade;
    }

    public double efficiencyUpgrade() {
        return efficiencyUpgrade;
    }

    public double recyclerNegateChance() {
        return recyclerNegateChance;
    }

    public double recyclerAddChance() {
        return recyclerAddChance;
    }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TurretStats)) return false;
            TurretStats other = (TurretStats) o;
            return enabled == other.enabled
                && baseRange == other.baseRange
                && baseFireRate == other.baseFireRate
                && baseDamage == other.baseDamage
                && powerUsage == other.powerUsage
                && Double.compare(baseAccuracyDeviation, other.baseAccuracyDeviation) == 0
                && maxSimultaneous == other.maxSimultaneous
                && Double.compare(damageAmp, other.damageAmp) == 0
                && Double.compare(fireRateUpgrade, other.fireRateUpgrade) == 0
                && rangeUpgrade == other.rangeUpgrade
                && Double.compare(accuracyUpgrade, other.accuracyUpgrade) == 0
                && Double.compare(efficiencyUpgrade, other.efficiencyUpgrade) == 0
                && Double.compare(recyclerNegateChance, other.recyclerNegateChance) == 0
                && Double.compare(recyclerAddChance, other.recyclerAddChance) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(enabled, baseRange, baseFireRate, baseDamage, powerUsage, baseAccuracyDeviation, maxSimultaneous, damageAmp, fireRateUpgrade, rangeUpgrade, accuracyUpgrade, efficiencyUpgrade, recyclerNegateChance, recyclerAddChance);
        }

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

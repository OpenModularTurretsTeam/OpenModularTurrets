package omtteam.openmodularturrets.core.base;

/**
 * What a turret base targets and how far. Ported from OMLib {@code TargetingSettings}.
 * <p>
 * {@code maxRange} is derived from the attached turrets and range upgrades; {@code range} is the player-chosen
 * range and is always kept within {@code [0, maxRange]}.
 */
public class TargetingSettings {
    private boolean targetPlayers;
    private boolean targetMobs;
    private boolean targetPassive;
    private int range;
    private int maxRange;

    /**
     * Negative values count as 0; {@code range} is limited to {@code maxRange}.
     */
    public TargetingSettings(boolean targetPlayers, boolean targetMobs, boolean targetPassive, int range, int maxRange) {
        this.targetPlayers = targetPlayers;
        this.targetMobs = targetMobs;
        this.targetPassive = targetPassive;
        this.maxRange = Math.max(0, maxRange);
        this.range = Math.max(0, Math.min(range, this.maxRange));
    }

    /** Defaults of a new turret base (1.12.2 {@code TurretBase} constructor): only hostile mobs. */
    public static TargetingSettings defaults() {
        return new TargetingSettings(false, true, false, 0, 0);
    }

    public boolean isTargetPlayers() {
        return targetPlayers;
    }

    public void setTargetPlayers(boolean targetPlayers) {
        this.targetPlayers = targetPlayers;
    }

    public boolean isTargetMobs() {
        return targetMobs;
    }

    public void setTargetMobs(boolean targetMobs) {
        this.targetMobs = targetMobs;
    }

    public boolean isTargetPassive() {
        return targetPassive;
    }

    public void setTargetPassive(boolean targetPassive) {
        this.targetPassive = targetPassive;
    }

    /** The range the player chose, in blocks; never above {@link #getMaxRange()}. */
    public int getRange() {
        return range;
    }

    /** Ignored if outside {@code [0, maxRange]}. */
    public void setRange(int range) {
        if (range >= 0 && range <= maxRange) {
            this.range = range;
        }
    }

    /** The longest range the attached turrets and range upgrades allow. */
    public int getMaxRange() {
        return maxRange;
    }

    /** Also lowers the range if it exceeds the new maximum. */
    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
        if (range > maxRange) {
            range = maxRange;
        }
    }
}

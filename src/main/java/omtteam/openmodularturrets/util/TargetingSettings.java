package omtteam.openmodularturrets.util;

/**
 * Created by Keridos on 17/08/17.
 * This Class
 */
public class TargetingSettings {
    private boolean targetPlayers, targetMobs, targetPassive;
    private int maxRange;

    public TargetingSettings(boolean targetPlayers, boolean targetMobs, boolean targetPassive, int maxRange) {
        this.targetPlayers = targetPlayers;
        this.targetMobs = targetMobs;
        this.targetPassive = targetPassive;
        this.maxRange = maxRange;
    }

    public boolean isTargetPlayers() {
        return targetPlayers;
    }

    public boolean isTargetMobs() {
        return targetMobs;
    }

    public boolean isTargetPassive() {
        return targetPassive;
    }

    public int getMaxRange() {
        return maxRange;
    }
}

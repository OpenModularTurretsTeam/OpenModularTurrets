package omtteam.openmodularturrets.util;

/**
 * Created by Keridos on 17/08/17.
 * This Class
 */
@SuppressWarnings("unused")
public class TargetingSettings {
    private final boolean targetPlayers;
    private final boolean targetMobs;
    private final boolean targetPassive;
    private final int maxRange;

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

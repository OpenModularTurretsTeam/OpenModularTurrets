package omtteam.openmodularturrets.core.settings;

/**
 * Configurable stats of a base tier (1.12.2 {@code BaseSetting}; hardness and blast resistance are block properties
 * and stay in the Minecraft layer). A record in 1.20.1+.
 */
public final class BaseTierStats {
    private final int maxCharge;
    private final int maxIo;
    private final int maxTurrets;

    public BaseTierStats(int maxCharge, int maxIo, int maxTurrets) {
        this.maxCharge = maxCharge;
        this.maxIo = maxIo;
        this.maxTurrets = maxTurrets;
    }

    public int maxCharge() {
        return maxCharge;
    }

    public int maxIo() {
        return maxIo;
    }

    public int maxTurrets() {
        return maxTurrets;
    }
}

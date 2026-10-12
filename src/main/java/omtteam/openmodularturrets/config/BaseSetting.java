package omtteam.openmodularturrets.config;

/**
 * Per-tier turret base settings. Fields and defaults ported from oldmod {@code handler/config/BaseSetting}.
 */
public class BaseSetting {
    public int baseMaxCharge;
    public int baseMaxIo;
    public int baseMaxTurrets;
    public int baseBlastResistance;
    public int baseHardness;

    public BaseSetting() {
    }

    public BaseSetting(int baseMaxCharge, int baseMaxIo, int baseMaxTurrets, int baseBlastResistance, int baseHardness) {
        this.baseMaxCharge = baseMaxCharge;
        this.baseMaxIo = baseMaxIo;
        this.baseMaxTurrets = baseMaxTurrets;
        this.baseBlastResistance = baseBlastResistance;
        this.baseHardness = baseHardness;
    }

    void validate(ConfigValidator v, String path) {
        baseMaxCharge = v.min(path + ".baseMaxCharge", baseMaxCharge, 1);
        baseMaxIo = v.min(path + ".baseMaxIo", baseMaxIo, 1);
        baseMaxTurrets = v.min(path + ".baseMaxTurrets", baseMaxTurrets, 1);
        baseBlastResistance = v.min(path + ".baseBlastResistance", baseBlastResistance, 1);
        baseHardness = v.min(path + ".baseHardness", baseHardness, 1);
    }
}

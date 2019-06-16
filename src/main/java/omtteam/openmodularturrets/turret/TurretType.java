package omtteam.openmodularturrets.turret;

import omtteam.openmodularturrets.handler.config.TurretSetting;

public class TurretType {
    private String internalName;
    private TurretSetting settings;

    public TurretType(String internalName, TurretSetting settings) {
        this.internalName = internalName;
        this.settings = settings;
    }

    public String getInternalName() {
        return internalName;
    }

    public TurretSetting getSettings() {
        return settings;
    }
}

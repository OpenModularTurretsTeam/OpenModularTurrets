package omtteam.openmodularturrets.util;

import omtteam.openmodularturrets.handler.config.TurretSetting;

public class TurretType {
    private final String internalName;
    private final TurretSetting settings;

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

package omtteam.openmodularturrets.util;

import omtteam.openmodularturrets.handler.OMTConfigHandler;

public class TurretType {
    private String internalName;
    private OMTConfigHandler.TurretSetting settings;

    public TurretType(String internalName, OMTConfigHandler.TurretSetting settings) {
        this.internalName = internalName;
        this.settings = settings;
    }

    public String getInternalName() {
        return internalName;
    }

    public OMTConfigHandler.TurretSetting getSettings() {
        return settings;
    }
}

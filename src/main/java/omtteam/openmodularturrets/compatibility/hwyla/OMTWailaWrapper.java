package omtteam.openmodularturrets.compatibility.hwyla;

import omtteam.omlib.compatibility.hwyla.OMLibWailaPlugin;

public class OMTWailaWrapper {
    private static OMTWailaWrapper instance;

    private OMTWailaWrapper() {

    }

    public static OMTWailaWrapper getInstance() {
        if (instance == null) {
            instance = new OMTWailaWrapper();
        }
        return instance;
    }

    public void register() {
        OMLibWailaPlugin.addDataProvider(new WailaTurretBaseHandler());
        OMLibWailaPlugin.addDataProvider(new WailaTurretHandler());
    }
}

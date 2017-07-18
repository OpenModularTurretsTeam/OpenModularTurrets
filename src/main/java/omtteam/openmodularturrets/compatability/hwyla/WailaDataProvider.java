package omtteam.openmodularturrets.compatability.hwyla;

import mcp.mobius.waila.api.IWailaRegistrar;

/**
 * Created by Keridos on 25/11/16.
 * This Class
 */
@SuppressWarnings("unused")
public class WailaDataProvider {
    public void register(IWailaRegistrar registrar) {
        WailaTurretBaseHandler.callbackRegister(registrar);
        WailaTurretBaseHandler.callbackRegister(registrar);
    }
}

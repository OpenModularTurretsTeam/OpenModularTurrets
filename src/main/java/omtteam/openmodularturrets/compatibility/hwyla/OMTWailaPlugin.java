package omtteam.openmodularturrets.compatibility.hwyla;

import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.WailaPlugin;


/**
 * Created by Keridos on 15/11/17.
 * This Class
 */

@WailaPlugin
public class OMTWailaPlugin implements IWailaPlugin {
    @Override
    public void register(IWailaRegistrar registrar) {
        WailaTurretBaseHandler.callbackRegister(registrar);
        WailaTurretHandler.callbackRegister(registrar);
    }
}

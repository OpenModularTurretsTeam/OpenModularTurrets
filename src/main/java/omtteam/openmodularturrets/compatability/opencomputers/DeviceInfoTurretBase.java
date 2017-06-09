package omtteam.openmodularturrets.compatability.opencomputers;

import li.cil.oc.api.driver.DeviceInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nico on 08/06/17.
 */
public class DeviceInfoTurretBase implements DeviceInfo {

    @Override
    public Map<String, String> getDeviceInfo() {
        Map<String, String> deviceInfo = new HashMap<>();
        deviceInfo.putIfAbsent("type", "Turret Base");
        deviceInfo.putIfAbsent("vendor", "Open Modules nc.");
        return deviceInfo;
    }
}

package omtteam.openmodularturrets.compatability.opencomputers;

/**
 * Created by Keridos on 13/07/17.
 * This Class
 */
public class OCCompat {
    private static OCCompat instance;

    private OCCompat() {
        registerDrivers();
    }

    public static OCCompat getInstance() {
        if (instance == null) {
            instance = new OCCompat();
        }
        return instance;
    }

    private void registerDrivers() {
        new DriverTurretBase().registerWrapper();
    }
}

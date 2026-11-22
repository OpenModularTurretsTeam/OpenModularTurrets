package omtteam.openmodularturrets.platform.neoforge.opencomputers;

import li.cil.oc.api.Driver;
import omtteam.openmodularturrets.OMT;

/**
 * OpenComputers: Rebooted (1.21.1 NeoForge only): registers the turret base driver, so an Adapter next to a base
 * offers it as the component {@code turret_base} (1.12: {@code compat/opencomputers}, registered by
 * {@code ModCompatibility.registerOCCompat}). Only loaded when OpenComputers is installed (compile-only, never
 * bundled).
 */
public final class OpenComputersCompat {
    public static final String MOD_ID = "opencomputers";

    private OpenComputersCompat() {
    }

    /**
     * Registers the driver; called once at common setup (OpenComputers drops drivers added before its own start-up and
     * refuses them after loading completed on a dedicated server).
     */
    public static void register() {
        Driver.add(new TurretBaseDriver());
        OMT.LOGGER.info("OpenComputers found: turret bases are components (turret_base)");
    }
}

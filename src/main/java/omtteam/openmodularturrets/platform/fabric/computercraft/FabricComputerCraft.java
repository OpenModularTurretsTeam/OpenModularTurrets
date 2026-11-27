package omtteam.openmodularturrets.platform.fabric.computercraft;

import dan200.computercraft.api.peripheral.PeripheralLookup;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.compat.computercraft.TurretBasePeripheral;
import omtteam.openmodularturrets.registry.ModBlockEntities;

/**
 * CC: Tweaked on Fabric: a computer next to a turret base (or a wired modem on it) sees the peripheral
 * {@code turret_base}. Only loaded when CC: Tweaked is installed (compile-only, never bundled).
 */
public final class FabricComputerCraft {
    public static final String MOD_ID = "computercraft";

    private FabricComputerCraft() {
    }

    /** Registers the peripheral for the turret base block entity, on every side. */
    public static void register() {
        PeripheralLookup.get().registerForBlockEntity((base, side) -> TurretBasePeripheral.of(base),
                ModBlockEntities.TURRET_BASE);
        OMT.LOGGER.info("CC: Tweaked found: turret bases are peripherals (turret_base)");
    }
}

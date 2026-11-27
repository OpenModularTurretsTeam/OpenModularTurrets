package omtteam.openmodularturrets.platform.neoforge.computercraft;

import dan200.computercraft.api.peripheral.PeripheralCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.compat.computercraft.TurretBasePeripheral;
import omtteam.openmodularturrets.registry.ModBlockEntities;

/**
 * CC: Tweaked on NeoForge: a computer next to a turret base (or a wired modem on it) sees the peripheral
 * {@code turret_base}. Only loaded when CC: Tweaked is installed (compile-only, never bundled).
 */
public final class NeoForgeComputerCraft {
    public static final String MOD_ID = "computercraft";

    private NeoForgeComputerCraft() {
    }

    /** Registers the peripheral for the turret base block entity, on every side; called from the capability event. */
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(PeripheralCapability.get(), ModBlockEntities.TURRET_BASE,
                (base, side) -> TurretBasePeripheral.of(base));
        OMT.LOGGER.info("CC: Tweaked found: turret bases are peripherals (turret_base)");
    }
}

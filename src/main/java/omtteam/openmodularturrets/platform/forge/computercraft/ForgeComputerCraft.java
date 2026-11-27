package omtteam.openmodularturrets.platform.forge.computercraft;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import net.minecraftforge.common.util.LazyOptional;
import omtteam.openmodularturrets.OMT;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;
import omtteam.openmodularturrets.compat.computercraft.TurretBasePeripheral;

/**
 * CC: Tweaked on Forge: a computer next to a turret base (or a wired modem on it) sees the peripheral
 * {@code turret_base}. Only loaded when CC: Tweaked is installed (compile-only, never bundled).
 */
public final class ForgeComputerCraft {
    public static final String MOD_ID = "computercraft";

    private ForgeComputerCraft() {
    }

    /**
     * Registers CC's peripheral provider for turret bases, on every side; called at common setup. The peripherals are
     * not cached: CC looks a peripheral up again when a neighbouring block changes, and two peripherals of one base
     * are equal, so a computer does not detach and attach again.
     */
    public static void register() {
        ForgeComputerCraftAPI.registerPeripheralProvider((level, pos, side) -> {
            if (level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
                TurretBasePeripheral peripheral = TurretBasePeripheral.of(base);
                if (peripheral != null) {
                    return LazyOptional.of(() -> peripheral);
                }
            }
            return LazyOptional.empty();
        });
        OMT.LOGGER.info("CC: Tweaked found: turret bases are peripherals (turret_base)");
    }
}

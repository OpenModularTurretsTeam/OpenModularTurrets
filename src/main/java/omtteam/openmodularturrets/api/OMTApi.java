package omtteam.openmodularturrets.api;

import net.minecraft.world.item.BlockItem;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.core.turret.AmmoType;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.turret.TurretTypes;
import omtteam.openmodularturrets.registry.ModBlocks;
import omtteam.openmodularturrets.registry.ModItems;
import omtteam.openmodularturrets.registry.TurretRegistry;

import java.util.Collection;
import java.util.Optional;

/**
 * Lookups for registered turrets. Only usable after all {@link OMTPlugin}s ran, i.e. not inside
 * {@link OMTPlugin#registerTurrets}.
 */
public final class OMTApi {
    private OMTApi() {
    }

    /** All turrets, built-in first. */
    public static Collection<TurretDefinition> turrets() {
        requireRegistered();
        return TurretRegistry.turrets();
    }

    public static Optional<TurretDefinition> turret(TurretType type) {
        requireRegistered();
        return TurretRegistry.find(type);
    }

    public static Optional<AmmoDefinition> ammo(AmmoType type) {
        requireRegistered();
        return TurretRegistry.ammo(type);
    }

    /** The block Open Modular Turrets registered for a turret type. */
    public static TurretBlock block(TurretType type) {
        requireRegistered();
        return ModBlocks.TURRETS.get(type);
    }

    /** The block item Open Modular Turrets registered for a turret type. */
    public static BlockItem item(TurretType type) {
        requireRegistered();
        return ModItems.TURRETS.get(type);
    }

    private static void requireRegistered() {
        if (!TurretTypes.isFrozen()) {
            throw new IllegalStateException("Turrets are still being registered");
        }
    }
}

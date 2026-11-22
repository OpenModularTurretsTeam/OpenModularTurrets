package omtteam.openmodularturrets.platform.neoforge.opencomputers;

import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverSidedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import omtteam.openmodularturrets.blockentity.TurretBaseBlockEntity;

/**
 * The OpenComputers driver for turret bases (1.12: {@code DriverTurretBase}): an Adapter next to a base of any tier
 * gets a {@link TurretBaseEnvironment}; whether a computer may use it is up to the base
 * ({@code TurretBaseLogic.acceptsComputers()}, checked by every command). The side is the Adapter's and plays no role.
 */
public final class TurretBaseDriver extends DriverSidedBlockEntity {

    @Override
    public Class<?> getBlockEntityClass() {
        return TurretBaseBlockEntity.class;
    }

    /** A new environment for the base at {@code pos} (OpenComputers wants a new one per call), or null without one. */
    @Override
    public ManagedEnvironment createEnvironment(Level level, BlockPos pos, Direction side) {
        MinecraftServer server = level.getServer();
        if (server != null && level.getBlockEntity(pos) instanceof TurretBaseBlockEntity base) {
            return new TurretBaseEnvironment(base, new NeoForgePlayerNames(server));
        }
        return null;
    }
}

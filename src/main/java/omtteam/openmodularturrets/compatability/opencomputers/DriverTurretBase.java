package omtteam.openmodularturrets.compatability.opencomputers;

import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.compatability.opencomputers.AbstractOMDriver;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by nico on 08/06/17.
 * The OpenComputers (Singleton) driver for a turret base.
 */
public class DriverTurretBase extends AbstractOMDriver {
    @Override
    public Class<?> getTileEntityClass() {
        return TurretBase.class;
    }

    @Override
    public ManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side) {
        TileEntity base = world.getTileEntity(pos);
        return (base != null && base instanceof TurretBase ? new ManagedEnvironmentTurretBase((TurretBase) base) : null);
    }
}

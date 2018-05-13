package omtteam.openmodularturrets.compatibility.opencomputers;

import li.cil.oc.api.network.ManagedEnvironment;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.compatibility.opencomputers.AbstractOMDriver;
import omtteam.openmodularturrets.reference.OMTNames;
import omtteam.openmodularturrets.reference.Reference;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by nico on 08/06/17.
 * The OpenComputers (Singleton) driver for a turret base.
 */
public class DriverTurretBase extends AbstractOMDriver {
    private static DriverTurretBase instance;

    private DriverTurretBase() {

    }

    public static DriverTurretBase getInstance() {
        if (instance == null) {
            instance = new DriverTurretBase();
        }
        return instance;
    }

    @Override
    public Class<?> clGetTileEntityClass() {
        return TurretBase.class;
    }

    @Override
    public ManagedEnvironment clCreateEnvironment(World world, BlockPos pos, EnumFacing side) {
        TileEntity base = world.getTileEntity(pos);
        return (base instanceof TurretBase ? new ManagedEnvironmentTurretBase((TurretBase) base) : null);
    }

    @Override
    protected String getName() {
        return Reference.MOD_ID + ":" + OMTNames.Blocks.turretBase;
    }
}

package openmodularturrets.compatability;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by Keridos on 05/02/2016.
 * This Class
 */
public class CCPeripheralProvider implements IPeripheralProvider {
    private static CCPeripheralProvider instance;

    private CCPeripheralProvider() {
    }

    public static CCPeripheralProvider getInstance() {
        if (instance == null) {
            instance = new CCPeripheralProvider();
        }
        return instance;
    }


    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        if (world.getTileEntity(x, y, z) instanceof TurretBase) {
            return (IPeripheral) world.getTileEntity(x, y, z);
        }
        return null;
    }
}

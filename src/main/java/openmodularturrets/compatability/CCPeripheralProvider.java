package openmodularturrets.compatability;

import net.minecraft.world.World;

import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import openmodularturrets.tileentity.turretbase.TurretBase;

/**
 * Created by Keridos on 05/02/2016. This Class
 */

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft")
public class CCPeripheralProvider implements IPeripheralProvider {

    private static CCPeripheralProvider instance;

    private CCPeripheralProvider() {}

    public static CCPeripheralProvider getInstance() {
        if (instance == null) {
            instance = new CCPeripheralProvider();
        }
        return instance;
    }

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side) {
        if (world.getTileEntity(x, y, z) instanceof TurretBase) {
            return (IPeripheral) world.getTileEntity(x, y, z);
        }
        return null;
    }
}

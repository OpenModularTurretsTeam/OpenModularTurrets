package openmodularturrets.compatability;


/*import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import openmodularturrets.tileentity.TurretBase;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = "ComputerCraft")
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

    @Optional.Method(modid = "ComputerCraft")
    @Override
    public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing side) {
        if (world.getTileEntity(pos) instanceof TurretBase) {
            return (IPeripheral) world.getTileEntity(pos);
        }
        return null;
    }
}
*/
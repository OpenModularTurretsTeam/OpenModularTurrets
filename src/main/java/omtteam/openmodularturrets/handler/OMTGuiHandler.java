package omtteam.openmodularturrets.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import omtteam.openmodularturrets.container.GeneratorContainer;
import omtteam.openmodularturrets.container.TurretBaseContainer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

import javax.annotation.Nullable;

public class OMTGuiHandler implements IGuiHandler {
    private final ClientGuis clientGuis;

    /** @param clientGuis the GUI screens, supplied by the proxy (screens only exist on the client) */
    public OMTGuiHandler(ClientGuis clientGuis) {
        this.clientGuis = clientGuis;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id) {
            case 1:
                return new TurretBaseContainer(player.inventory, (TurretBase) tileEntity);
            case 3:
                return tileEntity instanceof GeneratorTileEntity
                        ? new GeneratorContainer(player.inventory, (GeneratorTileEntity) tileEntity) : null;
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return clientGuis.getClientGuiElement(id, player, world, x, y, z);
    }

    /** Creates the GUI screen of a GUI id; only the client proxy has screens. */
    public interface ClientGuis {
        @Nullable
        Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z);
    }
}

package omtteam.openmodularturrets.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.container.GeneratorContainer;
import omtteam.openmodularturrets.container.TurretBaseContainer;
import omtteam.openmodularturrets.tileentity.base.TurretBase;
import omtteam.openmodularturrets.tileentity.misc.GeneratorTileEntity;

public class OMTGuiHandler implements IGuiHandler {
    private static OMTGuiHandler instance;

    private OMTGuiHandler() {
    }

    public static OMTGuiHandler getInstance() {
        if (instance == null) {
            instance = new OMTGuiHandler();
        }
        return instance;
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
        return OpenModularTurrets.proxy.getClientGuiElement(id, player, world, x, y, z);
    }
}

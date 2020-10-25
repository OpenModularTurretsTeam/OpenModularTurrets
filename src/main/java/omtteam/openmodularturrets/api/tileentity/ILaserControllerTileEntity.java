package omtteam.openmodularturrets.api.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.tileentity.turrets.LaserTurretTileEntity;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import org.lwjgl.util.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keridos on 17/05/17.
 * This interface is for a(n attachable) laser (disco ?) controller.
 */
@SuppressWarnings("ALL")
public interface ILaserControllerTileEntity extends ITurretBaseAddonTileEntity {
    /**
     * This should give back the lasers the connected base has
     *
     * @param world the World of the block
     * @param pos   the BlockPos of the block in the world
     * @return List of Lasers
     */
    default List<LaserTurretTileEntity> getLasers(World world, BlockPos pos) {
        List<TileEntity> list = WorldUtil.getTouchingTileEntities(world, TurretHeadUtil.getTurretBase(world, pos).getPos());
        List<LaserTurretTileEntity> lasers = new ArrayList<>();
        for (TileEntity te : list) {
            if (te instanceof LaserTurretTileEntity) {
                lasers.add((LaserTurretTileEntity) te);
            }
        }
        return lasers;
    }

    /**
     * Return the Color of the laser beam
     *
     * @return Color
     */
    Color getColorForLaser();
}

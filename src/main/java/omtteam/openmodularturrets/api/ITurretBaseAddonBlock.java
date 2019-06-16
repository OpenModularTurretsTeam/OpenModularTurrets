package omtteam.openmodularturrets.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

/**
 * Created by Keridos on 17/05/17.
 * This interface is for turret base addon blocks, like expanders, or turret heads.
 */
public interface ITurretBaseAddonBlock {

    /**
     * This should give the correct bounding box for the block based on its Facing.
     * It is used for example for the block building preview.
     *
     * @param facing the facing of the block
     * @param world  the World of the block
     * @param pos    the BlockPos of the block in the world
     * @return the correct bounding box for the block, based on its blockstate and pos.
     */
    AxisAlignedBB getBoundingBoxFromFacing(EnumFacing facing, World world, BlockPos pos);

    /**
     * This should give back the base that this addon block belongs to.
     *
     * @param world the World of the block
     * @param pos   the BlockPos of the block in the world
     * @return the corresponding base.
     */
    default TurretBase getBase(World world, BlockPos pos) {
        return TurretHeadUtil.getTurretBase(world, pos);
    }
}

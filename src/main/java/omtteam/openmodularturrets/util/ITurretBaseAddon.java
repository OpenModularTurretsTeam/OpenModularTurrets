package omtteam.openmodularturrets.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by Keridos on 17/05/17.
 * This Class
 */
public interface ITurretBaseAddon {

    AxisAlignedBB getBoundingBoxFromState(IBlockState blockState, World world, BlockPos pos);
    default TurretBase getBase(World world, BlockPos pos) {
        return TurretHeadUtil.getTurretBase(world, pos);
    }
}

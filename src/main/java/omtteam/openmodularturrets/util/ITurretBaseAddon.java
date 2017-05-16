package omtteam.openmodularturrets.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.openmodularturrets.tileentity.TurretBase;

/**
 * Created by Keridos on 17/05/17.
 * This Class
 */
public interface ITurretBaseAddon {

    default TurretBase getBase(World world, BlockPos pos) {
        return TurretHeadUtil.getTurretBase(world, pos);
    }
}

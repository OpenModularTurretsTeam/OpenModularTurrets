package omtteam.openmodularturrets.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.api.IOwnedBlockAddon;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

/**
 * Created by Keridos on 17/05/17.
 * This Class
 */
public interface ITurretBaseAddonTileEntity extends IOwnedBlockAddon {
    default TurretBase getBase(World world, BlockPos pos) {
        return TurretHeadUtil.getTurretBase(world, pos);
    }
}

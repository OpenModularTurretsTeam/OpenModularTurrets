package omtteam.openmodularturrets.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import omtteam.omlib.api.IOwnedBlockAddon;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

/**
 * Created by Keridos on 17/05/17.
 * This interface if for all tile entities of addons attached to turret bases.
 */
public interface ITurretBaseAddonTileEntity extends IOwnedBlockAddon {
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

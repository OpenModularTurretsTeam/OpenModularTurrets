package omtteam.openmodularturrets.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

/**
 * Created by Keridos on 08/08/17.
 * This interface should be implemented by tile entities that want to control turret bases.
 * Register them with base.registerController(ITurretBaseController instance) for them to be used.
 * For turning bases off, use setMode() on the base.
 */
public interface ITurretBaseController extends ITurretBaseAddonTileEntity {
    /**
     * Return true if the Entity given per parameter is a valid target.
     *
     * @param target the target
     * @param yaw    the yaw of the vector between turret and target
     * @param pitch  the pitch of the vector between turret and target
     * @return boolean
     */
    boolean isEntityValidTarget(Entity target, float yaw, float pitch);

    /**
     * Return the BlockPos of the tile entity implementing this.
     *
     * @return BlockPos
     */
    BlockPos getPositionOfBlock();
}

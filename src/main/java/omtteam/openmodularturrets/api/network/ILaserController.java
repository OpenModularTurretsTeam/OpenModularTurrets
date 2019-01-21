package omtteam.openmodularturrets.api.network;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import omtteam.omlib.api.network.IController;
import org.lwjgl.util.Color;

/**
 * Created by Keridos on 08/08/17.
 * This interface should be implemented by tile entities that want to control turret bases.
 * Register them with base.registerController(IBaseController instance) for them to be used.
 * Only one instance of this interface can be registered per base.
 */
@SuppressWarnings("ALL")
public interface ILaserController extends IController {
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
     * Return true if the controller overrides base modes.
     *
     * @return boolean
     */
    boolean overridesMode();

    /**
     * Return the BlockPos of the tile entity implementing this.
     *
     * @return BlockPos
     */
    BlockPos getPositionOfBlock();

    /**
     * Return the Color of the laser beam
     *
     * @return Color
     */
    Color getColorForLaser();
}

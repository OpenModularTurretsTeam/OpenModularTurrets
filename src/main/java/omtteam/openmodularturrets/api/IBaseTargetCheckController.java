package omtteam.openmodularturrets.api;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import omtteam.omlib.tileentity.EnumMachineMode;

/**
 * Created by Keridos on 08/08/17.
 * This interface should be implemented by tile entities that want to control turret bases.
 * Register them with base.registerController(IBaseTargetCheckController instance) for them to be used.
 * Turret Bases get additional API functions like getAllTargetsInRange. Only one of these can be registered per base.
 */
public interface IBaseTargetCheckController extends ITurretBaseAddonTileEntity {
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
     * Return true if the controller overrides Base modes. Should not be changed based on anything.
     *
     * @return boolean
     */
    boolean overridesMode();

    /**
     * Return the overriden mode if the controller overrides Base modes.
     *
     * @return overriding mode
     */
    EnumMachineMode getOverriddenMode();

    /**
     * Return the BlockPos of the tile entity implementing this.
     *
     * @return BlockPos
     */
    BlockPos getPositionOfBlock();
}

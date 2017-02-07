package omtteam.openmodularturrets.compatability.valkyrienwarfare;

import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ValkyrienWarfareHelper {

    /**
     * If the given BlockPos is part of an existing Ship, this returns the Ship Entity that it is managing
     *
     * @param worldFor
     * @param pos
     * @return
     */
    public static Entity getShipManagingBlock(World worldFor, BlockPos pos) {
        return ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldFor, pos);
    }

    /**
     * Converts the inputed Vec3d coordinate from World space to Ship space
     *
     * @param shipEntity
     * @param inShip
     * @return
     */
    public static Vec3d getVec3InWorldSpaceFromShipSpace(Entity shipEntity, Vec3d inShip) {
        PhysicsWrapperEntity physicsWrapper = (PhysicsWrapperEntity) shipEntity;
        return RotationMatrices.applyTransform(physicsWrapper.wrapping.coordTransform.lToWTransform, inShip);
    }

    /**
     * Converts the inputed Vec3d coordinate from Ship space to World space
     *
     * @param shipEntity
     * @param inWorld
     * @return
     */
    public static Vec3d getVec3InShipSpaceFromWorldSpace(Entity shipEntity, Vec3d inWorld) {
        PhysicsWrapperEntity physicsWrapper = (PhysicsWrapperEntity) shipEntity;
        return RotationMatrices.applyTransform(physicsWrapper.wrapping.coordTransform.wToLTransform, inWorld);
    }
}

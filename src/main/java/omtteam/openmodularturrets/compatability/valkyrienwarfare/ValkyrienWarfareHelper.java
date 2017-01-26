package omtteam.openmodularturrets.compatability.valkyrienwarfare;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.ValkyrienWarfareHooks;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.Collision.Polygon;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class ValkyrienWarfareHelper {

	/**
	 * If the given BlockPos is part of an existing Ship, this returns the Ship Entity that it is managing
	 * @param worldFor
	 * @param pos
	 * @return
	 */
	public static Entity getShipManagingBlock(World worldFor, BlockPos pos){
		return ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldFor, pos);
	}

	/**
	 * Converts the inputed Vec3d coordinate from World space to Ship space
	 * @param shipEntity
	 * @param inShip
	 * @return
	 */
	public static Vec3d getVec3InWorldSpaceFromShipSpace(Entity shipEntity, Vec3d inShip){
		PhysicsWrapperEntity physicsWrapper = (PhysicsWrapperEntity) shipEntity;
		Vec3d inWorld = RotationMatrices.applyTransform(physicsWrapper.wrapping.coordTransform.lToWTransform, inShip);
		return inWorld;
	}
	
	/**
	 * Converts the inputed Vec3d coordinate from Ship space to World space
	 * @param shipEntity
	 * @param inWorld
	 * @return
	 */
	public static Vec3d getVec3InShipSpaceFromWorldSpace(Entity shipEntity, Vec3d inWorld){
		PhysicsWrapperEntity physicsWrapper = (PhysicsWrapperEntity) shipEntity;
		Vec3d inShip = RotationMatrices.applyTransform(physicsWrapper.wrapping.coordTransform.wToLTransform, inWorld);
		return inShip;
	}
}

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

	public static boolean isBlockPosInShipSpace(World worldObj, BlockPos pos){
		return ValkyrienWarfareHooks.isBlockPartOfShip(worldObj, pos);
	}
	
	public static void doSomething(Entity shipEntity){
		PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEntity;
		
	}
	
	public static Entity getShipManagingBlock(World worldFor, BlockPos pos){
		return ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldFor, pos);
	}
	
	public static AxisAlignedBB getAABBInWorldSpaceFromShipSpace(Entity shipEntity, AxisAlignedBB aabbInShip){
		PhysicsWrapperEntity wrapper = (PhysicsWrapperEntity) shipEntity;
		//Makes a Polygon representing the input AABB, then applies all the vertices through a rotation matrix
		Polygon aabbPoly = new Polygon(aabbInShip, wrapper.wrapping.coordTransform.lToWTransform);
		//Even though the aabbPoly isn't axis aligned anymore, its fine to just make a new AABB using the max coords of it
		return aabbPoly.getEnclosedAABB();
	}
	
	public static Vec3d getVec3InWorldSpaceFromShipSpace(Entity shipEntity, Vec3d inShip){
		PhysicsWrapperEntity physicsWrapper = (PhysicsWrapperEntity) shipEntity;
		Vec3d inWorld = RotationMatrices.applyTransform(physicsWrapper.wrapping.coordTransform.lToWTransform, inShip);
		return inWorld;
	}
}

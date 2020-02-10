package omtteam.openmodularturrets.compatibility.valkyrienwarfare;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

public class VWUtil {
    public static BlockPos getTransformedBlockPos(TileEntity te) {
        BlockPos pos = te.getPos();
        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.
                    INSTANCE.getPhysicsEntityFromShipSpace(te.getWorld(), pos);
            if (physicsEntity != null) {
                pos = new BlockPos(physicsEntity.transformVector(new Vec3d(pos), TransformType.SUBSPACE_TO_GLOBAL));
            }
        }
        return pos;
    }
}

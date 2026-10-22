package omtteam.openmodularturrets.turret;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import omtteam.openmodularturrets.tileentity.TurretBaseWorld;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;

/**
 * Line of sight of turrets. Target selection is the core {@code TargetSelector}, run by the turret base.
 */
public class TurretTargetingUtils {
    private TurretTargetingUtils() {
    }

    /**
     * @param turret the turret trying to find a target
     * @param entity the entity to test
     * @return if the turret can see the target (the same check the base uses for targeting).
     */
    public static boolean canSeeTargetFromPos(TurretHead turret, EntityLivingBase entity) {
        Vec3d from = new Vec3d(turret.getPos().getX() + 0.5D, turret.getPos().getY() + 0.5D, turret.getPos().getZ() + 0.5D);
        Vec3d to = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        return TurretBaseWorld.isClear(turret.getWorld(), from, to);
    }
}

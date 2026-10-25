package omtteam.openmodularturrets.blockentity.turret;

import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Pose;
import omtteam.openmodularturrets.core.world.Vector3;

/**
 * Where a turret is in the world during this tick ({@link TurretBlockEntity#getMount()}). On a Sable contraption the
 * turret's block is stored in a plot far away and shown somewhere else; these are the values where it is shown.
 * Outside contraptions the pose is the identity and every value is the plain block position.
 *
 * @param pose     the contraption's pose this tick ({@link Pose#IDENTITY} outside contraptions)
 * @param face     the base face the turret is mounted on (in the plot, like the block state)
 * @param centre   world point the turret block's centre is shown at
 * @param up       world unit vector pointing out of the face
 * @param velocity the contraption's velocity at the centre, in blocks per tick ({@link Vec3#ZERO} outside
 *                 contraptions)
 */
public record TurretMount(Pose pose, Face face, Vec3 centre, Vec3 up, Vec3 velocity) {

    /** World point a plot point is shown at (ray origin and tip, teleport destination, addon weapons). */
    public Vec3 toWorld(Vec3 plotPoint) {
        return toVec3(pose.toWorld(toVector(plotPoint)));
    }

    /** Plot point shown at a world point (checking entities against a box in the plot, e.g. the melee area). */
    public Vec3 toLocal(Vec3 worldPoint) {
        return toVec3(pose.toLocal(toVector(worldPoint)));
    }

    /**
     * A world direction in the plot frame, for {@link omtteam.openmodularturrets.core.turret.TurretMath} (yaw and
     * pitch relative to the face).
     */
    public Vec3 toLocalDirection(Vec3 worldDirection) {
        return toVec3(pose.directionToLocal(toVector(worldDirection)));
    }

    public boolean isOnContraption() {
        return !pose.isIdentity();
    }

    static Vector3 toVector(Vec3 vec) {
        return new Vector3(vec.x, vec.y, vec.z);
    }

    static Vec3 toVec3(Vector3 vector) {
        return new Vec3(vector.x(), vector.y(), vector.z());
    }
}

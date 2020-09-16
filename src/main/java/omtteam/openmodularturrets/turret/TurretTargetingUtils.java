package omtteam.openmodularturrets.turret;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import omtteam.omlib.handler.OMConfig;
import omtteam.omlib.util.TargetingSettings;
import omtteam.omlib.util.world.Pos;
import omtteam.omlib.util.world.WorldUtil;
import omtteam.openmodularturrets.OpenModularTurrets;
import omtteam.openmodularturrets.api.lists.MobBlacklist;
import omtteam.openmodularturrets.api.lists.MobList;
import omtteam.openmodularturrets.api.lists.NeutralList;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.tileentity.turrets.TurretHead;
import omtteam.openmodularturrets.util.OMTUtil;
import org.apache.logging.log4j.Logger;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

import javax.annotation.Nullable;
import java.util.List;

import static omtteam.omlib.util.player.PlayerUtil.*;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getAimPitch;
import static omtteam.openmodularturrets.turret.TurretHeadUtil.getAimYaw;

public class TurretTargetingUtils {
    private TargetingSettings settings;
    private final Pos pos;
    private final TurretHead turret;

    public TurretTargetingUtils(TurretHead turret) {
        this.settings = turret.getTargetingSettings();
        this.pos = new Pos(turret.getPos());
        this.turret = turret;
    }

    public static boolean chebyshevDistance(TurretHead turret, EntityLivingBase entity) {
        Vec3d targetPos = new Vec3d(entity.posX, entity.posY, entity.posZ);
        Vec3d startPos = new Vec3d(turret.getPos());

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(turret.getWorld(),
                                                                                                        turret.getPos());
            if (physicsEntity != null) {
                startPos = physicsEntity.transformVector(startPos, TransformType.SUBSPACE_TO_GLOBAL);
            }
        }
        BlockPos pos = new BlockPos(startPos);
        return MathHelper.absMax(MathHelper.absMax(targetPos.x - pos.getX(), targetPos.y - pos.getY()),
                                 targetPos.z - pos.getZ()) > (turret.getBase().getRange());
    }

    /**
     * @param turret the turret trying to find a target
     * @param entity the entity to test
     * @return if the turret can see the target.
     */
    public static boolean canSeeTargetFromPos(TurretHead turret, EntityLivingBase entity) {
        Pos pos = new Pos(turret.getPos());
        Vec3d traceStart = new Vec3d(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(turret.getWorld(),
                                                                                                        turret.getPos());
            if (physicsEntity != null) {
                traceStart = physicsEntity.transformVector(traceStart, TransformType.SUBSPACE_TO_GLOBAL);
            }
        }

        Vec3d traceEnd = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        Vec3d vecDelta = new Vec3d(traceEnd.x - traceStart.x,
                                   traceEnd.y - traceStart.y,
                                   traceEnd.z - traceStart.z);

        // Normalize vector to the largest delta axis
        vecDelta = vecDelta.normalize();

        // Limit how many non solid block a turret can see through
        for (int i = 0; i < 100; i++) {
            // Offset start position toward the target to prevent self collision
            traceStart = traceStart.add(vecDelta);

            RayTraceResult traced = turret.getWorld().rayTraceBlocks(traceStart, traceEnd);

            if (traced != null && traced.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState hitBlock = turret.getWorld().getBlockState(traced.getBlockPos());

                // If non solid block is in the way then proceed to continue
                // tracing
                if ((traced.getBlockPos().equals(turret.getPos())) || (!hitBlock.getMaterial().isSolid() && MathHelper.absMax(
                        MathHelper.absMax(traceStart.x - traceEnd.x, traceStart.y - traceEnd.y),
                        traceStart.z - traceEnd.z) > 1)) {
                    // Start at new position and continue
                    traceStart = traced.hitVec;
                    continue;
                } else return false;
            }

            EntityLivingBase targeted = traced == null ? entity : traced.typeOfHit == RayTraceResult.Type.MISS ? entity : null;

            return targeted != null;
        }

        // If all above failed, the target cannot be seen
        return false;
    }

    public static boolean isEntityValidNeutral(TurretHead turret, EntityLivingBase possibleTarget) {
        if (turret.getBase().isAttacksNeutrals() && OMTConfig.TURRETS.globalCanTargetNeutrals) {
            return !possibleTarget.isDead && (possibleTarget instanceof EntityAnimal ||
                    possibleTarget instanceof EntityAmbientCreature || NeutralList.contains(possibleTarget));
        }
        return false;
    }

    public static boolean isEntityValidMob(TurretHead turret, EntityLivingBase possibleTarget) {
        if (turret.getBase().isAttacksMobs() && OMTConfig.TURRETS.globalCanTargetMobs) {
            return !possibleTarget.isDead && (possibleTarget.isCreatureType(EnumCreatureType.MONSTER, false) ||
                    MobList.contains(possibleTarget));
        }
        return false;
    }

    public static boolean isTargetAlreadyTargeted(TurretBase base, Entity entity) {
        for (TileEntity tileEntity : WorldUtil.getTouchingTileEntities(base.getWorld(), base.getPos())) {
            if (tileEntity instanceof TurretHead) {
                if (entity.equals(((TurretHead) tileEntity).target)) {
                    return true;
                }
            }
        }
        return false;
    }

    public TargetingSettings getSettings() {
        return settings;
    }

    public void setSettings(TargetingSettings settings) {
        this.settings = settings;
    }

    @Nullable
    public EntityLivingBase getBestEntity(List<EntityLivingBase> entityList) {
        int bestPriority = Integer.MIN_VALUE;
        EntityLivingBase bestEntity = null;
        for (EntityLivingBase entity : entityList) {
            if (!validTarget(entity)) {
                continue;
            }
            int tempPriority = 1;
            for (EnumTargetingPriority priority : EnumTargetingPriority.values()) {
                Integer[] priorities = turret.getPriorities();

                tempPriority *= EnumTargetingPriority.getValueByPriority(entity, pos, priority, settings, priorities);
            }

            if (tempPriority > bestPriority) {
                bestPriority = tempPriority;
                bestEntity = entity;
            }
        }
        return bestEntity;
    }

    private boolean validTarget(EntityLivingBase entity) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (!(settings.isTargetPlayers() && OMTConfig.TURRETS.globalCanTargetPlayers)
                    || isPlayerTrusted(player, turret) // Check if player is trusted)
                    || (isPlayerOP(player) && OMConfig.GENERAL.canOPAccessOwnedBlocks) // Check if player is OP and settings are set
                    || !OMTUtil.canDamagePlayer(player, turret.getBase()) // Check if we can damage the player
                    || player.isDead // Do not target dead players
            ) {
                return false; // return false if player fails checks.
            }
        }
        // Can the turret still see the target? (It's moving)
        if (!canSeeTargetFromPos(turret, entity)) {
            return false;
        }

        //Is the target out of range now?
        if (chebyshevDistance(turret, entity)) {
            return false;
        }

        if (OMConfig.GENERAL.debugLogging) {
            Logger logger = OpenModularTurrets.getLogger();
            logger.info("Targeting, EntityString: " + EntityList.getEntityString(entity));
            logger.info("Targeting, EntityKey: " + EntityList.getKey(entity));
        }

        if (EntityList.getEntityString(entity) != null && MobBlacklist.contains(EntityList.getEntityString(entity))) {
            return false;
        }

        if (turret.getBase().getController() != null && !turret.getBase().getController().isEntityValidTarget(entity, getAimYaw(entity, pos), getAimPitch(entity, pos))) {
            return false;
        }

        if (entity instanceof IEntityOwnable) {
            Entity entityOwned = ((IEntityOwnable) entity).getOwner();
            if (entityOwned instanceof EntityPlayer) {
                EntityPlayer owner = (EntityPlayer) entityOwned;
                if (isPlayerOwner(owner, turret) || isPlayerTrusted(owner, turret)) {
                    return false;
                }
            }
        } else if (entity instanceof EntityHorse) {
            if (((EntityHorse) entity).isTame()) {
                return false;
            }
        }

        if (turret.getBase().isMultiTargeting() && isTargetAlreadyTargeted(turret.getBase(), entity)) {
            return false;
        }

        if (!turret.isEntityValidTarget(entity)) {
            return false;
        }

        if (isEntityValidNeutral(turret, entity) && settings.isTargetPassive()) {
            return true;
        }

        if (isEntityValidMob(turret, entity) && settings.isTargetMobs()) {
            return true;
        }

        return entity instanceof EntityPlayer;
    }
}

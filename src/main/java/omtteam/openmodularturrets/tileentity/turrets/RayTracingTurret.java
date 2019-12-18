package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import omtteam.omlib.util.MathUtil;
import omtteam.omlib.util.RandomUtil;
import omtteam.omlib.util.WorldUtil;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.util.OMTUtil;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

import java.util.List;
import java.util.Random;

public abstract class RayTracingTurret extends AbstractDirectedTurret {

    public RayTracingTurret(int tier) {
        super(tier);
    }

    protected abstract void renderRay(Vec3d start, Vec3d end);

    protected abstract SoundEvent getHitSound();

    protected abstract float getDamageModifier(Entity entity);

    protected abstract float getNormalDamageFactor();

    protected abstract float getBypassDamageFactor();

    protected abstract void applyHitEffects(Entity entity);

    protected abstract void applyLaunchEffects();

    protected abstract void handleBlockHit(IBlockState hitBlock, BlockPos pos);

    @Override
    protected void doTargetedShot(Entity target, ItemStack ammo) {
        Vec3d targetPos = target.getPositionVector();
        double d0 = targetPos.x;
        double d1 = targetPos.y + target.getEyeHeight();
        double d2 = targetPos.z;
        if (ModCompatibility.ValkyrienWarfareLoaded) {

            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                    getPos());
            if (physicsEntity != null) {
                Vec3d targetPosInShip = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
                d0 = targetPosInShip.x;
                d1 = targetPosInShip.y + target.getEyeHeight();
                d2 = targetPosInShip.z;
            }
        }
        shootRay(d0, d1, d2, this.getActualTurretAccuracy());
    }

    @Override
    public boolean forceShot() {
        Vec3d direction = MathUtil.getVectorFromYawPitch(this.yaw, this.pitch).scale(5D);
        Vec3d baseVector = new Vec3d(this.getPos().getX() + 0.5D,
                                     this.getPos().getY() + 0.6D,
                                     this.getPos().getZ() + 0.5D);
        Vec3d result = baseVector.add(direction);
        shootRay(result.x, result.y, result.z, this.getActualTurretAccuracy());
        return true;
    }

    protected void shootRay(double adjustedX, double adjustedY, double adjustedZ, double accuracy) {
        // Consume energy
        base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - getPowerRequiredForNextShot(), null);
        this.applyLaunchEffects();

        // Create one ray per scatter-shot upgrade
        for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
            double xDev, yDev, zDev;
            boolean hit = false;
            // vector points at the target, baseVector is the origin of the raytrace
            Vec3d vector = new Vec3d(adjustedX, adjustedY, adjustedZ);
            Vec3d baseVector = new Vec3d(this.getPos().getX() + 0.5D,
                                         this.getPos().getY() + 0.6D,
                                         this.getPos().getZ() + 0.5D);
            if (ModCompatibility.ValkyrienWarfareLoaded) {

                IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                        getPos());
                if (physicsEntity != null) {
                    vector = physicsEntity.transformVector(vector, TransformType.SUBSPACE_TO_GLOBAL);
                    baseVector = physicsEntity.transformVector(baseVector, TransformType.SUBSPACE_TO_GLOBAL);
                }
            }
            // Calculate deviation based on targets height and its distance to the turret
            double deviationModifier = 1D * (target.height < 0.5 ? 1.5D : 1D)
                    * ((vector.distanceTo(baseVector) * 0.5D / (this.getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(base, this))) + 0.3D);

            xDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;
            yDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;
            zDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;

            vector = vector.addVector(xDev, yDev, zDev);
            baseVector = baseVector.add(vector.subtract(baseVector).normalize().scale(0.75D));

            // Play Sound
            this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                                      OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
            // Raytrace to see if/where it hits a block
            RayTraceResult blockTraceResult = world.rayTraceBlocks(baseVector, vector, false, true, false);

            // Raytrace entities hit
            List<RayTraceResult> entityHits = WorldUtil.traceEntities(null, baseVector, vector, world);
            double blockRange = blockTraceResult != null ? blockTraceResult.hitVec.distanceTo(baseVector) : 500;

            for (RayTraceResult result : entityHits) { // Loop through all entities
                Entity entity = result.entityHit;
                if (baseVector.distanceTo(result.hitVec) <= blockRange) { // if entity is nearer than block hit
                    if (onHitEntity(entity)) {
                        this.renderRay(baseVector, vector);
                        hit = true;
                        break;
                    }
                }
            }
            if (!hit) {
                if (blockTraceResult != null && blockTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (baseVector.distanceTo(blockTraceResult.hitVec) <= blockRange) {
                        this.renderRay(baseVector, blockTraceResult.hitVec);
                        handleBlockHit(world.getBlockState(blockTraceResult.getBlockPos()), blockTraceResult.getBlockPos());
                    }
                }else {
                    this.renderRay(baseVector, vector.add(vector.subtract(baseVector).scale(2D)));
                }
            }
        }
    }

    protected boolean onHitEntity(Entity entity) {
        if (entity != null && !entity.getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {
            if (entity instanceof EntityPlayer) {
                if (OMTUtil.canDamagePlayer((EntityPlayer) entity, base)) { // Player hit handling
                    damageEntity(entity);
                    applyHitEffects(entity);
                    entity.hurtResistantTime = -1;
                    this.getWorld().playSound(null, entity.getPosition(), this.getHitSound(), SoundCategory.AMBIENT,
                                              OMTConfig.TURRETS.turretSoundVolume, RandomUtil.random.nextFloat() + 0.5F);
                    return true;
                } else {
                    return false;
                }
            } else if (OMTUtil.canDamageEntity(entity, base)) {  // Entity hit Handling
                OMTUtil.setTagsForTurretHit(entity, base);
                damageEntity(entity);
                applyHitEffects(entity);
                entity.hurtResistantTime = -1;
                this.getWorld().playSound(null, entity.getPosition(), this.getHitSound(), SoundCategory.AMBIENT,
                                          OMTConfig.TURRETS.turretSoundVolume, RandomUtil.random.nextFloat() + 0.5F);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    protected void damageEntity(Entity entity) {
        float damageModifier = this.getDamageModifier(entity); // The damage modifier of the turret based on entity
        float damage = this.getTurretType().getSettings().baseDamage * damageModifier;
        int fakeDrops = TurretHeadUtil.getFakeDropsLevel(base);

        if (this.getTurretDamageAmpBonus() * TurretHeadUtil.getAmpLevel(base) > 0) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) entity;
                damage += ((int) elb.getHealth() * this.getTurretDamageAmpBonus() * TurretHeadUtil.getAmpLevel(base));
            }
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) entity;
            // Attack 2 times, once with normal, once with Bypassing Damage
            elb.attackEntityFrom(new NormalDamageSource(this.getTurretType().getInternalName(), fakeDrops, base,
                                                        (WorldServer) this.getWorld(), false), damage * this.getNormalDamageFactor());
            elb.attackEntityFrom(new ArmorBypassDamageSource(this.getTurretType().getInternalName(), fakeDrops, base,
                                                             (WorldServer) this.getWorld(), false), damage * this.getBypassDamageFactor());
        }
    }
}

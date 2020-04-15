package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.RandomUtil;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

import java.util.Random;

import static omtteam.omlib.util.MathUtil.getVelocityVectorFromYawPitch;

public abstract class ProjectileTurret extends AbstractDirectedTurret {
    public ProjectileTurret(int tier) {
        super(tier);
    }

    protected abstract float getProjectileGravity();

    public abstract TurretProjectile createProjectile(World world, Entity target, ItemStack ammo);

    /**
     * Tracks target and shoots at it
     */
    @Override
    protected void doTargetedShot(EntityLivingBase target, ItemStack ammo) {
        // Update target tracking (Player entity not setting motion data when moving via movement keys)
        double speedX = target instanceof EntityPlayerMP ? targetSpeedX : target.posX - target.prevPosX;
        double speedY = target instanceof EntityPlayerMP ? targetSpeedY : target.posY - target.prevPosY;
        double speedZ = target instanceof EntityPlayerMP ? targetSpeedZ : target.posZ - target.prevPosZ;

        // Calculate speed from displacement from last tick (Or use tracking data if target is player)
        double d0 = target.posX - (this.pos.getX() + 0.5);
        double d1 = target.posY + (double) target.height * 0.5F - (this.pos.getY() + 0.5);
        double d2 = target.posZ - (this.pos.getZ() + 0.5);
        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                                                                                                        getPos());
            if (physicsEntity != null) {
                Vec3d targetPos = target.getPositionVector();
                Vec3d targetPosInShip = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
                d0 = targetPosInShip.x - (this.pos.getX() + 0.5);
                d1 = targetPosInShip.y + (double) target.height * 0.5F - (this.pos.getY() + 0.5);
                d2 = targetPosInShip.z - (this.pos.getZ() + 0.5);
                Vec3d targetSpeed = new Vec3d(speedX, speedY, speedZ);
                Vec3d targetSpeedInShip = physicsEntity.rotateVector(targetSpeed, TransformType.GLOBAL_TO_SUBSPACE);
                speedX = targetSpeedInShip.x;
                speedY = targetSpeedInShip.y;
                speedZ = targetSpeedInShip.z;
            }
        }

        double dist = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        double inaccuracy = (this.getActualTurretAccuracyDeviation()) / 20D;

        // Adjust new firing coordinate according to target speed
        double time = dist / (this.getProjectileGravity() == 0.00F ? 3.0 : 1.6);
        double adjustedX = d0 + speedX * time;
        double adjustedY = d1 + speedY * time;
        double adjustedZ = d2 + speedZ * time;
        if (ModCompatibility.ValkyrienWarfareLoaded) {
            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                                                                                                        getPos());
            if (physicsEntity != null) {
                Vec3d trace = new Vec3d(adjustedX, adjustedY, adjustedZ);
                Vec3d trace2 = physicsEntity.rotateVector(trace, TransformType.SUBSPACE_TO_GLOBAL);
                adjustedX = trace2.x;
                adjustedY = trace2.y;
                adjustedZ = trace2.z;
            }
        }

        // Calculate projectile speed scaling factor to travel to adjusted destination on time
        double dist2 = MathHelper.sqrt(adjustedX * adjustedX + adjustedY * adjustedY + adjustedZ * adjustedZ);
        float speedFactor = (float) (dist2 / dist);

        // Now that we have a trajectory, throw something at it
        shootProjectile(adjustedX, adjustedY - 0.1F, adjustedZ, 3.0F * speedFactor, (float) inaccuracy, ammo);
    }

    /**
     * Just shoots, no aiming required
     */
    @Override
    public boolean forceShot() {
        if (this instanceof RocketTurretTileEntity && OMTConfig.TURRETS.canRocketsHome) return false;
        if (ticks < (this.getTurretBaseFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base, this)))) {
            return false;
        }
        //Finally, try to shoot if criteria is met.
        ItemStack ammo = getAmmoStack();

        // Is there ammo?
        if (ammo == ItemStack.EMPTY && this.requiresAmmo() && OMTConfig.TURRETS.doTurretsNeedAmmo) {
            return false;
        }

        base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - getPowerRequiredForNextShot(), null);

        for (int i = 0; i <= this.cachedScattershot; i++) {
            double accuracy = this.getActualTurretAccuracyDeviation() / 20D;
            TurretProjectile projectile = this.createProjectile(this.getWorld(), target, ammo);
            projectile.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
            if (projectile.gravity == 0.00F) {
                Vec3d velocity = getVelocityVectorFromYawPitch(this.pitch, this.yaw, 3.0F);
                projectile.shoot(velocity.x, velocity.y, velocity.z, (float) velocity.lengthVector(), (float) accuracy);
            } else {
                projectile.rotationYaw = this.yaw;
                projectile.rotationPitch = this.pitch;
                Vec3d velocity = getVelocityVectorFromYawPitch(projectile.rotationYaw, projectile.rotationPitch, 1.6F);
                projectile.motionX = velocity.x + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.motionY = velocity.y + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.motionZ = velocity.z + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.prevRotationYaw = projectile.rotationYaw;
                projectile.prevRotationPitch = projectile.rotationPitch;
            }
            this.getWorld().spawnEntity(projectile);
        }
        this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                                  (float) OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
        ticks = 0;

        return true;
    }

    /**
     * Attempts to create and throw a projectile
     */
    protected void shootProjectile(double adjustedX, double adjustedY, double adjustedZ, float speedFactor, float inaccuracy, ItemStack ammo) {
        // Consume energy
        base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - getPowerRequiredForNextShot(), null);

        // Create one projectile per scatter-shot upgrade
        for (int i = 0; i <= this.cachedScattershot; i++) {
            // Create a projectile, consuming ammo if applicable
            TurretProjectile projectile = this.createProjectile(this.getWorld(), this.target, ammo);

            // Set projectile starting position
            projectile.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);

            //If the turret is on a Ship, it needs to change to World coordinates from Ship coordinates
            if (ModCompatibility.ValkyrienWarfareLoaded) {
                // If the turret is on a Ship, it needs to change to World coordinates from Ship
                // coordinates
                IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                                                                                                            getPos());
                if (physicsEntity != null) {
                    Vec3d oldPos = projectile.getPositionVector();
                    Vec3d newPos = physicsEntity.transformVector(oldPos, TransformType.SUBSPACE_TO_GLOBAL);
                    projectile.setPosition(newPos.x, newPos.y, newPos.z);
                }
            }

            // Set projectile heading
            projectile.shoot(adjustedX, adjustedY, adjustedZ, speedFactor, inaccuracy);

            // Play sounds
            if ((projectile.amp_level = TurretHeadUtil.getAmpLevel(base)) != 0) {
                this.getWorld().playSound(null, this.pos, ModSounds.amped, SoundCategory.BLOCKS,
                                          (float) OMTConfig.TURRETS.turretSoundVolume, RandomUtil.random.nextFloat() + 0.5F);
                projectile.isAmped = true;
            }
            this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                                      (float) OMTConfig.TURRETS.turretSoundVolume, RandomUtil.random.nextFloat() + 0.5F);

            // Spawn entity
            this.getWorld().spawnEntity(projectile);
        }
    }
}

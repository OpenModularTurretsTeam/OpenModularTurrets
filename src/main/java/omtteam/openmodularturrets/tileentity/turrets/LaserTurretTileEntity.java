package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.omlib.api.render.ColorOM;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.render.MessageRenderRay;
import omtteam.omlib.util.EntityUtil;
import omtteam.omlib.util.RandomUtil;
import omtteam.omlib.util.WorldUtil;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.OMTUtil;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import java.util.List;
import java.util.Random;

public class LaserTurretTileEntity extends TurretHead {
    private ColorOM color = new ColorOM(1F, 0.1F, 0, 0.38F);

    public LaserTurretTileEntity() {
        super();
        this.turretTier = 5;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfig.TURRETS.laser_turret.getBaseRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfig.TURRETS.laser_turret.getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfig.TURRETS.laser_turret.getBaseFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfig.TURRETS.laser_turret.getBaseAccuracyDeviation();
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfig.TURRETS.laser_turret.getDamageAmp();
    }

    @Override
    public boolean requiresAmmo() {
        return false;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return false;
    }

    @Override
    public ItemStack getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return null;
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.laserLaunchSound;
    }

    @Override
    protected void doTargetedShot(Entity target, ItemStack ammo) {
        shootLaser(target.posX, target.posY + target.getEyeHeight(), target.posZ, getTurretAccuracy(), target);
    }

    protected void shootLaser(double adjustedX, double adjustedY, double adjustedZ, double accuracy, Entity target) {
        // Consume energy
        base.setEnergyStored(base.getEnergyLevel(EnumFacing.DOWN) - getPowerRequiredForNextShot());

        // Create one projectile per scatter-shot upgrade
        for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
            double xDev, yDev, zDev;

            Vec3d vector = new Vec3d(adjustedX, adjustedY, adjustedZ);
            Vec3d baseVector = new Vec3d(this.getPos().getX() + 0.5D,
                                         this.getPos().getY() + 0.6D,
                                         this.getPos().getZ() + 0.5D);
            double deviationModifier = 1D * (target.height < 0.5 ? 1.5D : 1D)
                    * ((vector.distanceTo(baseVector) * 0.5D / (this.getTurretRange() + TurretHeadUtil.getRangeUpgrades(base, this))) + 0.3D);

            xDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;
            yDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;
            zDev = RandomUtil.random.nextGaussian() * 0.035D * accuracy * deviationModifier;

            vector = vector.addVector(xDev, yDev, zDev);
            baseVector = baseVector.add(vector.subtract(baseVector).normalize().scale(0.75D));

            // Play Sound
            this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                                      OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);

            RayTraceResult blockTraceResult = world.rayTraceBlocks(baseVector, vector, false, true, false);
            List<RayTraceResult> entityHits = WorldUtil.traceEntities(null, baseVector, vector, world);
            double blockRange = blockTraceResult != null ? blockTraceResult.hitVec.distanceTo(baseVector) : 500;
            for (RayTraceResult result : entityHits) {
                Entity entity = result.entityHit;
                if (baseVector.distanceTo(result.hitVec) <= blockRange) {
                    if (onHitEntity(entity)) {
                        OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                                new MessageRenderRay(baseVector, vector, color, 5, true),
                                new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                                baseVector.x, baseVector.y, baseVector.z, 120));
                        return;
                    }
                }
            }
            OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                    new MessageRenderRay(baseVector, vector.add(vector.subtract(baseVector).scale(2D)), color, 5, true),
                    new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                    baseVector.x, baseVector.y, baseVector.z, 120));

        }
    }

    boolean onHitEntity(Entity entity) {
        if (entity != null && !entity.getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {
            if (entity instanceof EntityPlayer) {
                if (OMTUtil.canDamagePlayer((EntityPlayer) entity, base)) {
                    damageEntity(entity);
                    entity.hurtResistantTime = -1;
                    Random random = RandomUtil.random;
                    this.getWorld().playSound(null, entity.getPosition(), ModSounds.laserHitSound, SoundCategory.AMBIENT,
                                              OMTConfig.TURRETS.turretSoundVolume, random.nextFloat() + 0.5F);
                    return true;
                } else {
                    return false;
                }
            } else if (OMTUtil.canDamageEntity(entity, base)) {
                OMTUtil.setTagsForTurretHit(entity, base);
                damageEntity(entity);
                entity.hurtResistantTime = -1;
                Random random = RandomUtil.random;
                this.getWorld().playSound(null, entity.getPosition(), ModSounds.laserHitSound, SoundCategory.AMBIENT,
                                          OMTConfig.TURRETS.turretSoundVolume, random.nextFloat() + 0.5F);
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void damageEntity(Entity entity) {
        float damageModifier = (30 - EntityUtil.getEntityArmor(entity)) / 20F + 0.3F; //0.8x to 1.8x damage multiplicator
        float damage = OMTConfig.TURRETS.laser_turret.getBaseDamage() * damageModifier;

        if (this.getTurretDamageAmpBonus() * TurretHeadUtil.getAmpLevel(base) > 0) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase elb = (EntityLivingBase) entity;
                damage += ((int) elb.getHealth() * getTurretDamageAmpBonus() * TurretHeadUtil.getAmpLevel(base));
            }
        }

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase) entity;
            elb.attackEntityFrom(new NormalDamageSource("laser", TurretHeadUtil.getFakeDropsLevel(base),
                                                        base, (WorldServer) this.getWorld(), false), damage);
        }
    }
}

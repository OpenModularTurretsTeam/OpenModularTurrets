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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.omlib.util.RandomUtil;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.OMTUtil;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import org.lwjgl.util.Color;

import java.util.Random;

public class LaserTurretTileEntity extends TurretHead {
    private Color color = new Color(255, 32, 0, 128);

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
        return OMTConfig.TURRETS.laser_turret.getBaseAccuracyDeviation() / 10;
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

        double d0 = target.posX - (this.pos.getX() + 0.5);
        double d1 = target.posY + (double) target.height * 0.5F - (this.pos.getY() + 0.5);
        double d2 = target.posZ - (this.pos.getZ() + 0.5);
        double dist = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
        double inaccuracy = this.getTurretAccuracy() * (1 - TurretHeadUtil.getAccuraccyUpgrades(base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base));
    }

    @Override
    protected void shootProjectile(double adjustedX, double adjustedY, double adjustedZ, float speedFactor, float accuracy, ItemStack ammo) {
        // Consume energy
        base.setEnergyStored(base.getEnergyLevel(EnumFacing.DOWN) - getPowerRequiredForNextShot());

        // Create one projectile per scatter-shot upgrade
        for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
            double x, y, z;

            x = adjustedX + RandomUtil.random.nextGaussian() * 0.007499999832361937D * (double) accuracy;
            y = adjustedY + RandomUtil.random.nextGaussian() * 0.007499999832361937D * (double) accuracy;
            z = adjustedZ + RandomUtil.random.nextGaussian() * 0.007499999832361937D * (double) accuracy;
            Vec3d vector = new Vec3d(x, y, z);

            // Play Sound
            this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                                      OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);

            RayTraceResult result = world.rayTraceBlocks(new Vec3d(this.getBase().getPos().getX(),
                                                                   this.getBase().getPos().getY(),
                                                                   this.getBase().getPos().getZ()), vector, false, true, true);
        }
    }

    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {
        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid()) {
            // Go through non solid block
            return;
        }
    }

    public void onHitEntity(Entity entity) {
        if (entity != null && !entity.getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {

            int damage = OMTConfig.TURRETS.laser_turret.getBaseDamage();

            Random random = RandomUtil.random;
            this.getWorld().playSound(null, entity.getPosition(), ModSounds.laserHitSound, SoundCategory.AMBIENT,
                                      OMTConfig.TURRETS.turretSoundVolume, random.nextFloat() + 0.5F);
            if (getTurretDamageAmpBonus() > 0) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getTurretDamageAmpBonus()));
                }
            }

            if (entity instanceof EntityPlayer) {
                if (OMTUtil.canDamagePlayer((EntityPlayer) entity, base)) {
                    //damageEntity(entity);
                    entity.hurtResistantTime = -1;
                } else {
                    return;
                }
            } else if (OMTUtil.canDamageEntity(entity, base)) {
                OMTUtil.setTagsForTurretHit(entity, base);
                //damageEntity(entity);
                entity.hurtResistantTime = -1;
            } else {
                return;
            }
        }
    }
}

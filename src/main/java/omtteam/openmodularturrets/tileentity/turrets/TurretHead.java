package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nullable;
import java.util.Random;

import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;

public abstract class TurretHead extends TileEntityBase implements ITickable {
    int ticks;
    int targetingTicks;
    public float rotationXY;
    public float rotationXZ;
    public float baseFitRotationX;
    public float baseFitRotationZ;
    int turretTier;
    public TurretBase base;
    private boolean hasSetSide = false;
    public Entity target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    private boolean playedDeploy = false;
    private int ticksWithoutTarget;
    private double targetLastX = 0;
    private double targetLastY = 0;
    private double targetLastZ = 0;

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new SPacketUpdateTileEntity(this.pos, 2, var1);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound var1 = pkt.getNbtCompound();
        readFromNBT(var1);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setFloat("rotationXY", rotationXY);
        nbtTagCompound.setFloat("rotationXZ", rotationXZ);
        nbtTagCompound.setInteger("ticksBeforeFire", ticks);
        nbtTagCompound.setBoolean("shouldConceal", shouldConceal);
        super.writeToNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        this.rotationXY = par1.getFloat("rotationXY");
        this.rotationXZ = par1.getFloat("rotationXZ");
        this.shouldConceal = par1.getBoolean("shouldConceal");
    }

    void setSide() {
        if (hasSetSide || worldObj == null) {
            return;
        }

        if (worldObj.getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
        }
    }

    Entity getTargetWithMinRange() {
        return TurretHeadUtil.getTargetWithMinimumRange(base, worldObj, base.getyAxisDetect(), this.pos,
                getTurretRange() + TurretHeadUtil.getRangeUpgrades(base), this);
    }

    Entity getTargetWithoutEffect() {
        return TurretHeadUtil.getTargetWithoutSlowEffect(base, worldObj, base.getyAxisDetect(), this.pos,
                getTurretRange() + TurretHeadUtil.getRangeUpgrades(base),
                this);
    }

    private Entity getTarget() {
        return TurretHeadUtil.getTarget(base, worldObj, base.getyAxisDetect(), this.pos,
                getTurretRange() + TurretHeadUtil.getRangeUpgrades(base), this);
    }

    protected abstract int getTurretRange();

    TurretBase getBase() {
        return TurretHeadUtil.getTurretBase(worldObj, this.pos);
    }

    public float getRotationXY() {
        return rotationXY;
    }

    public void setRotationXY(float rotationXY) {
        this.rotationXY = rotationXY;
    }

    public float getRotationXZ() {
        return rotationXZ;
    }

    public void setRotationXZ(float rotationXZ) {
        this.rotationXZ = rotationXZ;
    }

    private float getDistanceToEntity(Entity p_70032_1_) {
        float f = (float) (this.getPos().getX() - p_70032_1_.posX);
        float f1 = (float) (this.getPos().getY() - p_70032_1_.posY);
        float f2 = (float) (this.getPos().getZ() - p_70032_1_.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    protected abstract int getTurretPowerUsage();

    protected abstract int getTurretFireRate();

    protected abstract double getTurretAccuracy();

    protected abstract boolean requiresAmmo();

    protected abstract boolean requiresSpecificAmmo();

    protected abstract ItemStack getAmmo();

    protected abstract TurretProjectile createProjectile(World world, Entity target, ItemStack ammo);

    protected abstract SoundEvent getLaunchSoundEffect();

    boolean chebyshevDistance(Entity target, TurretBase base) {
        return MathHelper.abs_max(MathHelper.abs_max(target.posX - this.getPos().getX(), target.posY - this.getPos().getY()),
                target.posZ - this.getPos().getZ()) > (getTurretRange() + TurretHeadUtil.getRangeUpgrades(
                base));
    }

    private int getPowerRequiredForNextShot() {
        return Math.round(this.getTurretPowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));
    }

    private void updateRotationAnimation() {
        if (rotationAnimation >= 360F) {
            rotationAnimation = 0F;
        }
        rotationAnimation = rotationAnimation + 0.03F;
    }

    @Override
    public void update() {

        setSide();
        this.base = getBase();

        if (worldObj.isRemote) {
            updateRotationAnimation();
            return;
        }

        if (ticks % 5 == 0) {
            worldObj.notifyBlockUpdate(this.pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos),3);
        }

        ticks++;

        //Base checks
        if (base == null || base.getTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
        } else {
            if (base.isAttacksPlayers() && base.isActive() && ConfigHandler.globalCanTargetPlayers) {
                TurretHeadUtil.warnPlayers(base, base.getWorld(), base.getyAxisDetect(), this.pos, getTurretRange());
            }

            //Real time tick updates
            TurretHeadUtil.updateSolarPanelAddon(base);
            concealmentChecks();

            if (!base.isActive()) {
                return;
            }

            //turret tick rate;
            if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            // power check
            if ((base.getEnergyStored(EnumFacing.DOWN) < getPowerRequiredForNextShot())) {
                return;
            }

            // is there a target, and Has it died in the previous tick?
            if (target == null || target.isDead || this.getWorld().getEntityByID(
                    target.getEntityId()) == null || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTarget();

                // did we even get a target previously?
                if (target == null) {
                    return;
                }

                // Start tracking if target is acquired
                targetLastX = target.prevPosX;
                targetLastY = target.prevPosY;
                targetLastZ = target.prevPosZ;

            }

            //set where the turret is aiming at.
            this.rotationXZ = TurretHeadUtil.getAimYaw(target, this.pos) + 3.2F;
            this.rotationXY = TurretHeadUtil.getAimPitch(target, this.pos);

            // Update target tracking (Player entity not setting motion data when moving via movement keys)
            double targetSpeedX = 0;
            double targetSpeedY = 0;
            double targetSpeedZ = 0;
            if (target != null && target instanceof EntityPlayerMP) {
                targetSpeedX = target.posX - targetLastX;
                targetSpeedY = target.posY - targetLastY;
                targetSpeedZ = target.posZ - targetLastZ;
                targetLastX = target.posX;
                targetLastY = target.posY;
                targetLastZ = target.posZ;
            }

            // has cooldown passed?
            if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base)))) {
                return;
            }

            //Player checks: is target in creative mode?
            if (target != null && target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (TurretHeadUtil.isTrustedPlayer(entity.getUniqueID(),
                        base) || entity.capabilities.isCreativeMode || !base.isAttacksPlayers()) {
                    target = null;
                    return;
                }
            }

            // Can the turret still see the target? (It's moving)
            if (target != null) {
                if (!TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) target)) {
                    target = null;
                    return;
                }
            }

            //Is the target out of range now?
            if (target != null) {
                if (chebyshevDistance(target, base)) {
                    target = null;
                    return;
                }
            }

            //Finally, try to shoot if criteria is met.
            ItemStack ammo = null;
            if (this.requiresAmmo()) {
                if (this.requiresSpecificAmmo()) {
                    for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                        ammo = TurretHeadUtil.useSpecificItemStackItemFromBase(base, this.getAmmo());
                        if (ammo == null) {
                            ammo = TurretHeadUtil.getSpecificItemFromInvExpanders(worldObj, this.getAmmo(), base);
                        }
                    }
                } else {
                    for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                        ammo = TurretHeadUtil.useAnyItemStackFromBase(base);
                        if (ammo == null) {
                            ammo = TurretHeadUtil.getAnyItemFromInvExpanders(worldObj, base);
                        }
                    }
                }

                // Is there ammo?
                if (ammo == null) {
                    return;
                }
            }

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - getPowerRequiredForNextShot());

            for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                TurretProjectile projectile = this.createProjectile(this.getWorld(), target, ammo);

                projectile.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);

                Random random = new Random();

//                if ((projectile.amp_level = TurretHeadUtil.getAmpLevel(base)) != 0) {
//                    worldObj.playSoundEffect(this.pos.getX(), this.pos.getY(), this.pos.getZ(), Reference.MOD_ID + ":amped",
//                            ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);
//                    projectile.isAmped = true;
//                }

                // Calculate speed from displacement from last tick (Or use tracking data if target is player)
                double speedX = target instanceof EntityPlayerMP ? targetSpeedX : target.posX - target.prevPosX;
                double speedY = target instanceof EntityPlayerMP ? targetSpeedY : target.posY - target.prevPosY;
                double speedZ = target instanceof EntityPlayerMP ? targetSpeedZ : target.posZ - target.prevPosZ;
                double d0 = target.posX - projectile.posX;
                double d1 = target.posY + (double) target.getEyeHeight() - projectile.posY;
                double d2 = target.posZ - projectile.posZ;
                double dist = MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
                float f1 = (float) dist * (0.2F * (getDistanceToEntity(target) * 0.04F));
                double accuracy = this.getTurretAccuracy() * (1 - TurretHeadUtil.getAccuraccyUpgrades(
                        base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base));

                // Adjust new firing coordinate according to target speed
                double time = dist / (projectile.gravity == 0.00F ? 3.0 : 1.6);
                double adjustedX = d0 + speedX * time;
                double adjustedY = d1 + speedY * time;
                double adjustedZ = d2 + speedZ * time;

                // Calculate projectile speed scaling factor to travel to adjusted destination on time
                double dist2 = MathHelper.sqrt_double(adjustedX * adjustedX + adjustedY * adjustedY + adjustedZ * adjustedZ);
                float speedFactor = (float) (dist2 / dist);

                if (projectile.gravity == 0.00F) {
                    projectile.setThrowableHeading(adjustedX, adjustedY,
                            adjustedZ, 3.0F * speedFactor, (float) accuracy);
                } else {
                    projectile.setThrowableHeading(adjustedX, adjustedY,
                            adjustedZ, 1.6F * speedFactor, (float) accuracy);
                }
                worldObj.playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                        ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
                this.getWorld().spawnEntityInWorld(projectile);
            }
            ticks = 0;
        }
    }

    void concealmentChecks() {
        if (base != null && base.shouldConcealTurrets) {
            if (!shouldConceal && target == null && ticksWithoutTarget >= 40) {
                ticksWithoutTarget = 0;
                shouldConceal = true;
                playedDeploy = false;
                worldObj.playSound(null, this.pos, ModSounds.turretRetractSound, SoundCategory.BLOCKS,
                        ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
                worldObj.setBlockState(this.pos,worldObj.getBlockState(pos).withProperty(CONCEALED, true), 3);
            } else {
                ticksWithoutTarget++;
            }

            if (base != null && target != null) {
                ticksWithoutTarget = 0;
                shouldConceal = false;

                if (!playedDeploy) {
                    worldObj.playSound(null, this.pos, ModSounds.turretDeploySound, SoundCategory.BLOCKS,
                            ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
                    playedDeploy = true;
                    worldObj.setBlockState(this.pos,worldObj.getBlockState(pos).withProperty(CONCEALED, false), 3);
                }
            }
        } else {
            this.shouldConceal = false;
            worldObj.setBlockState(this.pos,worldObj.getBlockState(pos).withProperty(CONCEALED, false), 3);
        }
    }
}

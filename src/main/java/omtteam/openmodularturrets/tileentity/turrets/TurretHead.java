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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.util.RandomUtil;
import omtteam.omlib.util.compat.ItemStackTools;
import omtteam.omlib.util.compat.MathTools;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.compatibility.valkyrienwarfare.ValkyrienWarfareHelper;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nullable;
import java.util.Random;

import static omtteam.omlib.util.MathUtil.getVelocityVectorFromYawPitch;
import static omtteam.omlib.util.PlayerUtil.isPlayerTrusted;
import static omtteam.omlib.util.compat.WorldTools.spawnEntity;
import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;


public abstract class TurretHead extends TileEntityBase implements ITickable {
    int ticks;
    int targetingTicks;
    public float pitch;
    public float yaw;
    public float baseFitRotationX;
    public float baseFitRotationZ;
    int turretTier;
    protected TurretBase base;
    private boolean hasSetSide = false;
    public Entity target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    private boolean playedDeploy = false;
    private boolean autoFire = false;
    private int ticksWithoutTarget;
    private double targetLastX = 0;
    private double targetLastY = 0;
    private double targetLastZ = 0;
    private double targetSpeedX = 0;
    private double targetSpeedY = 0;
    private double targetSpeedZ = 0;

    private float maxPitch = 360;
    private float maxYaw = 360;
    private float minPitch = 0;
    private float minYaw = 0;


    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new SPacketUpdateTileEntity(this.pos, 2, var1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound var1 = pkt.getNbtCompound();
        readFromNBT(var1);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setFloat("pitch", pitch);
        nbtTagCompound.setFloat("yaw", yaw);
        nbtTagCompound.setInteger("ticksBeforeFire", ticks);
        nbtTagCompound.setBoolean("shouldConceal", shouldConceal);
        nbtTagCompound.setBoolean("autoFire", autoFire);
        nbtTagCompound.setFloat("maxPitch", maxPitch);
        nbtTagCompound.setFloat("minPitch", minPitch);
        nbtTagCompound.setFloat("maxYaw", maxYaw);
        nbtTagCompound.setFloat("minYaw", minYaw);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        this.pitch = par1.getFloat("pitch");
        this.yaw = par1.getFloat("yaw");
        this.shouldConceal = par1.getBoolean("shouldConceal");
        this.autoFire = par1.getBoolean("autoFire");
        //this.maxPitch = par1.getFloat("maxPitch");
        //this.minPitch = par1.getFloat("minPitch");
        //this.maxYaw = par1.getFloat("maxYaw");
        //this.minYaw = par1.getFloat("minYaw");
    }

    void setSide() {
        if (hasSetSide) {
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.hasSetSide = true;
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.hasSetSide = true;
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (this.getWorld().getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
        }
    }

    Entity getTargetWithMinRange() {
        return TurretHeadUtil.getTargetWithMinimumRange(base, this.getWorld(), this.pos,
                Math.min(getTurretRange() + TurretHeadUtil.getRangeUpgrades(base), base.getCurrentMaxRange()), this);
    }

    Entity getTargetWithoutEffect() {
        return TurretHeadUtil.getTargetWithoutSlowEffect(base, this.getWorld(), this.pos,
                Math.min(getTurretRange() + TurretHeadUtil.getRangeUpgrades(base), base.getCurrentMaxRange()),
                this);
    }

    private Entity getTarget() {
        return TurretHeadUtil.getTarget(base, this.getWorld(), this.pos,
                Math.min(getTurretRange() + TurretHeadUtil.getRangeUpgrades(base), base.getCurrentMaxRange()), this);
    }

    public abstract int getTurretRange();

    TurretBase getBaseFromWorld() {
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
    }

    public TurretBase getBase() {
        return base;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getMaxPitch() {
        return maxPitch;
    }

    public void setMaxPitch(float maxPitch) {
        this.maxPitch = maxPitch;
    }

    public float getMinPitch() {
        return minPitch;
    }

    public void setMinPitch(float minPitch) {
        this.minPitch = minPitch;
    }

    public float getMaxYaw() {
        return maxYaw;
    }

    public void setMaxYaw(float maxYaw) {
        this.maxYaw = maxYaw;
    }

    public float getMinYaw() {
        return minYaw;
    }

    public void setMinYaw(float minYaw) {
        this.minYaw = minYaw;
    }

    public boolean getAutoFire() {
        return autoFire;
    }

    public void setAutoFire(boolean autoFire) {
        this.autoFire = autoFire;
    }

    public abstract int getTurretPowerUsage();

    public abstract int getTurretFireRate();

    public abstract double getTurretAccuracy();

    public abstract double getTurretDamageAmpBonus();

    protected abstract boolean requiresAmmo();

    protected abstract boolean requiresSpecificAmmo();

    protected abstract float getProjectileGravity();

    public abstract ItemStack getAmmo();

    protected abstract TurretProjectile createProjectile(World world, Entity target, ItemStack ammo);

    protected abstract SoundEvent getLaunchSoundEffect();

    boolean chebyshevDistance(Entity target, TurretBase base) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            Entity shipEntity = ValkyrienWarfareHelper.getShipManagingBlock(this.getWorld(), this.getPos());

            if (shipEntity != null) {
                //The turret is on a Ship, time to convert the coordinates; converting the target positions to local ship space
                targetPos = ValkyrienWarfareHelper.getVec3InShipSpaceFromWorldSpace(shipEntity, targetPos);
            }
        }

        return MathTools.abs_max(MathTools.abs_max(targetPos.xCoord - this.getPos().getX(), targetPos.yCoord - this.getPos().getY()),
                targetPos.zCoord - this.getPos().getZ()) > (this.getBaseFromWorld().getCurrentMaxRange());
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

    private ItemStack getAmmoStack() {
        ItemStack ammo = ItemStackTools.getEmptyStack();
        if (this.requiresAmmo()) {
            if (this.requiresSpecificAmmo()) {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getSpecificItemStackItemFromBase(base, this.getAmmo());
                    if (ammo == ItemStackTools.getEmptyStack()) {
                        ammo = TurretHeadUtil.getSpecificItemFromInvExpanders(this.getWorld(), this.getAmmo(), base);
                    }
                }
            } else {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getDisposableAmmoFromBase(base);
                    if (ammo == ItemStackTools.getEmptyStack()) {
                        ammo = TurretHeadUtil.getDisposableAmmoFromInvExpander(this.getWorld(), base);
                    }
                }
            }
        }
        return ammo;
    }

    private boolean isTargetInYawPitch(Entity entity) {
        while (yaw > 360 || pitch > 360 || yaw < 0 || pitch < 0) {
            if (yaw > 360) {
                yaw -= 360;
            }
            if (pitch > 360) {
                pitch -= 360;
            }
            if (yaw < 0) {
                yaw += 360;
            }
            if (pitch < 0) {
                pitch += 360;
            }
        }
        return minYaw <= yaw && yaw <= maxYaw && minPitch <= pitch && pitch <= maxPitch;
    }

    @Override
    public void update() {
        setSide();
        this.base = getBaseFromWorld();

        //Is this the client?
        if (this.getWorld().isRemote) {
            updateRotationAnimation();
            return;
        }

        //Is the turret head block still there?
        if (!(this.getWorld().getBlockState(this.getPos()).getBlock() instanceof BlockAbstractTurretHead)) {
            return;
        }

        //Send a block update after every 5 ticks of inactivity?
        if (this.ticks % 5 == 0) {
            this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(pos), this.getWorld().getBlockState(pos), 3);
        }
        this.ticks++;

        //Is turret base present and valid?
        if (this.base == null || this.base.getTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
            return;
        }

        //Is turret base active?
        if (!base.isActive()) {
            return;
        }

        //Real time tick updates
        TurretHeadUtil.updateSolarPanelAddon(base);
        concealmentChecks();

        //Is there enough power to shoot?
        if ((base.getEnergyLevel(EnumFacing.DOWN) < getPowerRequiredForNextShot())) {
            return;
        }

        //Warn players
        if (base.isAttacksPlayers() && ConfigHandler.globalCanTargetPlayers) {
            TurretHeadUtil.warnPlayers(base, base.getWorld(), this.pos, getTurretRange());
        }
        // Update target tracking (Player entity not setting motion data when moving via movement keys)

        if (target != null && target instanceof EntityPlayerMP) {
            targetSpeedX = target.posX - targetLastX;
            targetSpeedY = target.posY - targetLastY;
            targetSpeedZ = target.posZ - targetLastZ;
            targetLastX = target.posX;
            targetLastY = target.posY;
            targetLastZ = target.posZ;
        }

        //turret tick rate;
        if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
            targetingTicks++;
        } else {
            //Validate current target, get a new one if necessary
            targetingChecks();

            //Aim at target
            if (target != null && isTargetInYawPitch(target)) {
                this.yaw = TurretHeadUtil.getAimYaw(this.target, this.pos);
                this.pitch = TurretHeadUtil.getAimPitch(this.target, this.pos);
            }

            targetingTicks = 0;
        }

        if (target != null || this.autoFire) {
            // has cooldown passed?
            if (this.ticks < (this.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base)))) {
                return;
            }

            // Is there ammo?
            ItemStack ammo = getAmmoStack();
            if (ammo == ItemStackTools.getEmptyStack() && this.requiresAmmo()) {
                //TODO: Play some kind of clicking sound?
                return;
            }

            //Finally, try to shoot if criteria is met.
            if (target != null) {
                doTargetedShot(this.target, ammo);
            } else if (this.autoFire) {
                doBlindShot(ammo);
            }

            //If we made it this far, reset ticks to zero?
            this.ticks = 0;
        }
    }

    /**
     * Tracks target and shoots at it
     */
    private void doTargetedShot(Entity target, ItemStack ammo) {
        // Update target tracking (Player entity not setting motion data when moving via movement keys)
        double speedX = target instanceof EntityPlayerMP ? targetSpeedX : target.posX - target.prevPosX;
        double speedY = target instanceof EntityPlayerMP ? targetSpeedY : target.posY - target.prevPosY;
        double speedZ = target instanceof EntityPlayerMP ? targetSpeedZ : target.posZ - target.prevPosZ;

        // Calculate speed from displacement from last tick (Or use tracking data if target is player)
        double d0 = target.posX - (this.pos.getX() + 0.5);
        double d1 = target.posY + (double) target.getEyeHeight() - (this.pos.getY() + 0.5);
        double d2 = target.posZ - (this.pos.getZ() + 0.5);
        double dist = MathTools.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        double accuracy = this.getTurretAccuracy() * (1 - TurretHeadUtil.getAccuraccyUpgrades(base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base));

        // Adjust new firing coordinate according to target speed
        double time = dist / (this.getProjectileGravity() == 0.00F ? 3.0 : 1.6);
        double adjustedX = d0 + speedX * time;
        double adjustedY = d1 + speedY * time;
        double adjustedZ = d2 + speedZ * time;

        // Calculate projectile speed scaling factor to travel to adjusted destination on time
        double dist2 = MathTools.sqrt_double(adjustedX * adjustedX + adjustedY * adjustedY + adjustedZ * adjustedZ);
        float speedFactor = (float) (dist2 / dist);

        // Now that we have a trajectory, throw something at it
        shootProjectile(adjustedX, adjustedY - 0.1F, adjustedZ, 3.0F * speedFactor, (float) accuracy, ammo);
    }

    /**
     * Just shoots, no aiming required
     */
    private void doBlindShot(ItemStack ammo) {
        if (this instanceof RocketTurretTileEntity && ConfigHandler.canRocketsHome) {
            return;
        }

        // Work out a trajectory based on current yaw/pitch
        Vec3d velocity = getVelocityVectorFromYawPitch(this.pitch, this.yaw, 3.0F);
        double adjustedX = velocity.xCoord;
        double adjustedY = velocity.yCoord;
        double adjustedZ = velocity.zCoord;
        float speedFactor = (float) velocity.lengthVector();
        double accuracy = this.getTurretAccuracy() * (1 - TurretHeadUtil.getAccuraccyUpgrades(base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base));

        // Now that we have a trajectory, throw something at it
        shootProjectile(adjustedX, adjustedY, adjustedZ, speedFactor, (float) accuracy, ammo);
    }

    /**
     * Set this.autoFire to true instead. TODO: This is for a single shot only.
     */
    @Deprecated
    public boolean forceShot() {
        if (this instanceof RocketTurretTileEntity && ConfigHandler.canRocketsHome) return false;
        if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base)))) {
            return false;
        }
        //Finally, try to shoot if criteria is met.
        ItemStack ammo = getAmmoStack();

        // Is there ammo?
        if (ammo == ItemStackTools.getEmptyStack() && this.requiresAmmo()) {
            return false;
        }

        base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - getPowerRequiredForNextShot());

        for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
            double accuracy = this.getTurretAccuracy() * (1 - TurretHeadUtil.getAccuraccyUpgrades(
                    base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base));
            TurretProjectile projectile = this.createProjectile(this.getWorld(), target, ammo);
            projectile.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
            if (projectile.gravity == 0.00F) {
                Vec3d velocity = getVelocityVectorFromYawPitch(this.pitch, this.yaw, 3.0F);
                projectile.setThrowableHeading(velocity.xCoord, velocity.yCoord, velocity.zCoord, (float) velocity.lengthVector(), (float) accuracy);
            } else {
                projectile.rotationYaw = this.yaw;
                projectile.rotationPitch = this.pitch;
                Vec3d velocity = getVelocityVectorFromYawPitch(projectile.rotationYaw, projectile.rotationPitch, 1.6F);
                projectile.motionX = velocity.xCoord + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.motionY = velocity.yCoord + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.motionZ = velocity.zCoord + RandomUtil.random.nextGaussian() * 0.007499999832361937D * accuracy;
                projectile.prevRotationYaw = projectile.rotationYaw;
                projectile.prevRotationPitch = projectile.rotationPitch;
            }
            spawnEntity(this.getWorld(), projectile);
        }
        this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
        ticks = 0;

        return true;
    }

    /**
     * Attempts to create and throw a projectile
     */
    private void shootProjectile(double adjustedX, double adjustedY, double adjustedZ, float speedFactor, float accuracy, ItemStack ammo) {
        // Consume energy
        base.setEnergyStored(base.getEnergyLevel(EnumFacing.DOWN) - getPowerRequiredForNextShot());

        // Create one projectile per scatter-shot upgrade
        for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
            // Create a projectile, consuming ammo if applicable
            TurretProjectile projectile = this.createProjectile(this.getWorld(), this.target, ammo);

            // Set projectile starting position
            projectile.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);

            //If the turret is on a Ship, it needs to change to World coordinates from Ship coordinates
            if (ModCompatibility.ValkyrienWarfareLoaded) {
                Entity shipEntity = ValkyrienWarfareHelper.getShipManagingBlock(this.getWorld(), this.getPos());
                if (shipEntity != null) {
                    Vec3d inShipPos = new Vec3d(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5);
                    Vec3d inWorldPos = ValkyrienWarfareHelper.getVec3InWorldSpaceFromShipSpace(shipEntity, inShipPos);
                    projectile.setPosition(inWorldPos.xCoord, inWorldPos.yCoord, inWorldPos.zCoord);
                }
            }

            // Set projectile heading
            projectile.setThrowableHeading(adjustedX, adjustedY, adjustedZ, speedFactor, accuracy);

            // Play sounds
            if ((projectile.amp_level = TurretHeadUtil.getAmpLevel(base)) != 0) {
                this.getWorld().playSound(null, this.pos, ModSounds.amped, SoundCategory.BLOCKS,
                        ConfigHandler.getTurretSoundVolume(), RandomUtil.random.nextFloat() + 0.5F);
                projectile.isAmped = true;
            }
            this.getWorld().playSound(null, this.pos, this.getLaunchSoundEffect(), SoundCategory.BLOCKS,
                    ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);

            // Spawn entity
            spawnEntity(this.getWorld(), projectile);
        }
    }

    private void targetingChecks() {
        // is there a target, and has it died in the previous tick?
        if (this.target == null || this.target.isDead || this.getWorld().getEntityByID(this.target.getEntityId()) == null || ((EntityLivingBase) this.target).getHealth() <= 0.0F) {
            this.target = getTarget();
        }

        // did we get a target, and is it valid?
        if (this.target != null) {
            //Player checks: is target in creative mode?
            if (this.target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (isPlayerTrusted(entity, base) || entity.capabilities.isCreativeMode || !base.isAttacksPlayers()) {
                    this.target = null;
                    return;
                }
            }

            // Can the turret still see the target? (It's moving)
            if (!TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) this.target)) {
                this.target = null;
                return;
            }

            //Is the target out of range now?
            if (chebyshevDistance(target, base)) {
                this.target = null;
            }
        }
    }

    void concealmentChecks() {
        if (base != null && base.shouldConcealTurrets) {
            if (!shouldConceal && (target == null || !TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) target)) && ticksWithoutTarget >= 40) {
                ticksWithoutTarget = 0;
                shouldConceal = true;
                playedDeploy = false;
                this.getWorld().playSound(null, this.pos, ModSounds.turretRetractSound, SoundCategory.BLOCKS,
                        ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
                this.getWorld().setBlockState(this.pos, this.getWorld().getBlockState(pos).withProperty(CONCEALED, true), 3);
            } else {
                ticksWithoutTarget++;
            }

            if (base != null && target != null) {
                ticksWithoutTarget = 0;
                shouldConceal = false;

                if (!playedDeploy) {
                    this.getWorld().playSound(null, this.pos, ModSounds.turretDeploySound, SoundCategory.BLOCKS,
                            ConfigHandler.getTurretSoundVolume(), new Random().nextFloat() + 0.5F);
                    playedDeploy = true;
                    this.getWorld().setBlockState(this.pos, this.getWorld().getBlockState(pos).withProperty(CONCEALED, false), 3);
                }
            }
        } else {
            this.shouldConceal = false;
            this.getWorld().setBlockState(this.pos, this.getWorld().getBlockState(pos).withProperty(CONCEALED, false), 3);
        }
    }
}

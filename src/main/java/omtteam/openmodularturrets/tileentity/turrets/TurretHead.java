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
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.openmodularturrets.api.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import omtteam.openmodularturrets.util.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static omtteam.omlib.util.player.PlayerUtil.isPlayerTrusted;
import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;

public abstract class TurretHead extends TileEntityBase implements ITickable, ITurretBaseAddonTileEntity {
    public float pitch;
    public float yaw;
    public float baseFitRotationX;
    public float baseFitRotationZ;
    public Entity target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    protected TurretBase base;
    protected int ticks;
    protected int targetingTicks;
    protected int turretTier;
    private boolean hasSetSide = false;
    private EnumFacing turretBase;
    protected boolean playedDeploy = false;
    protected boolean autoFire = false;
    protected int ticksWithoutTarget;
    protected double targetLastX = 0;
    protected double targetLastY = 0;
    protected double targetLastZ = 0;
    protected double targetSpeedX = 0;
    protected double targetSpeedY = 0;
    protected double targetSpeedZ = 0;

    protected float maxPitch = 360;
    protected float maxYaw = 360;
    protected float minPitch = 0;
    protected float minYaw = 0;

    public TurretHead(int turretTier) {
        this.turretTier = turretTier;
    }

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

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return base;
    }

    // Set the rotation to fit the turret against the base and store its direction
    boolean setSide() {
        if (hasSetSide && !this.getWorld().isBlockLoaded(this.getPos().offset(turretBase))) {
            return false;
        }
        if (hasSetSide) {
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.east()) && this.getWorld().getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.turretBase = EnumFacing.EAST;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.west()) && this.getWorld().getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.turretBase = EnumFacing.WEST;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.south()) && this.getWorld().getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.turretBase = EnumFacing.SOUTH;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.north()) && this.getWorld().getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.NORTH;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.up()) && this.getWorld().getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.UP;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.down()) && this.getWorld().getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.DOWN;
            this.hasSetSide = true;
            return true;
        }
        return false;
    }

    Entity getTargetWithMinRange() {
        return TurretHeadUtil.getTargetWithMinimumRange(base, this.getWorld(), this.pos,
                                                        Math.min(getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(base, this), base.getCurrentMaxRange()), this);
    }

    Entity getTargetWithoutEffect() {
        return TurretHeadUtil.getTargetWithoutSlowEffect(base, this.getWorld(), this.pos,
                                                         Math.min(getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(base, this), base.getCurrentMaxRange()),
                                                         this);
    }

    private Entity getTarget() {
        return TurretHeadUtil.getTarget(base, this.getWorld(), this.pos,
                                        Math.min(getTurretBaseRange() + TurretHeadUtil.getRangeUpgrades(base, this), base.getCurrentMaxRange()), this);
    }

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

    public int getTurretBasePowerUsage() {
        return this.getTurretType().getSettings().getPowerUsage();
    }

    public int getTurretBaseFireRate() {
        return this.getTurretType().getSettings().getBaseFireRate();
    }

    public double getBaseTurretAccuracy() {
        return this.getTurretType().getSettings().getBaseAccuracyDeviation();
    }

    public int getTurretBaseRange() {
        return this.getTurretType().getSettings().getBaseRange();
    }

    public double getTurretDamageAmpBonus() {
        return this.getTurretType().getSettings().getDamageAmp();
    }

    public double getActualTurretAccuracy() {
        return 100F - (100F - this.getBaseTurretAccuracy())
                * (1.0F + TurretHeadUtil.getAccuraccyUpgrades(this.getBase(), this))
                * (1.0F - (TurretHeadUtil.getScattershotUpgrades(this.getBase())) / 25F);
    }

    protected abstract boolean requiresAmmo();

    protected abstract boolean requiresSpecificAmmo();

    public TurretType getTurretType() {
        BlockAbstractTurretHead block = (BlockAbstractTurretHead) this.getWorld().getBlockState(this.getPos()).getBlock();
        return block.getTurretType();
    }

    @Nullable
    public abstract ItemStack getAmmo();

    @Nonnull
    protected abstract SoundEvent getLaunchSoundEffect();

    boolean chebyshevDistance(Entity target) {
        Vec3d targetPos = new Vec3d(target.posX, target.posY, target.posZ);

        /*if (ModCompatibility.ValkyrienWarfareLoaded) {
            Entity shipEntity = ValkyrienWarfareHelper.getShipManagingBlock(this.getWorld(), this.getPos());

            if (shipEntity != null) {
                //The turret is on a Ship, time to convert the coordinates; converting the target positions to local ship space
                targetPos = ValkyrienWarfareHelper.getVec3InShipSpaceFromWorldSpace(shipEntity, targetPos);
            }
        } */

        return MathHelper.absMax(MathHelper.absMax(targetPos.x - this.getPos().getX(), targetPos.y - this.getPos().getY()),
                                 targetPos.z - this.getPos().getZ()) > (this.getBaseFromWorld().getCurrentMaxRange());
    }

    protected int getPowerRequiredForNextShot() {
        return Math.round(this.getTurretBasePowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));
    }

    protected void updateRotationAnimation() {
        if (rotationAnimation >= 360F) {
            rotationAnimation = 0F;
        }
        rotationAnimation = rotationAnimation + 0.03F;
    }

    protected ItemStack getAmmoStack() {
        ItemStack ammo = ItemStack.EMPTY;
        if (this.requiresAmmo()) {
            if (this.requiresSpecificAmmo()) {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getSpecificItemStackItemFromBase(base, this.getAmmo(), this);
                    if (ammo == ItemStack.EMPTY) {
                        ammo = TurretHeadUtil.getSpecificItemFromInvExpanders(this.getWorld(), this.getAmmo(), base, this);
                    }
                }
            } else {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getDisposableAmmoFromBase(base);
                    if (ammo == ItemStack.EMPTY) {
                        ammo = TurretHeadUtil.getDisposableAmmoFromInvExpander(this.getWorld(), base);
                    }
                }
            }
        }
        return ammo;
    }

    protected boolean isTargetInYawPitch(Entity entity) {
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
        if (!setSide()) return;
        this.base = getBaseFromWorld();

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
            this.target = null;
            return;
        }
        //Is this the client?
        if (this.getWorld().isRemote) {
            updateRotationAnimation();
            return;
        }

        if (this.ticks % 5 == 0) {
            this.getWorld().notifyBlockUpdate(this.pos, this.getWorld().getBlockState(pos), this.getWorld().getBlockState(pos), 3);
        }

        //Real time tick updates
        TurretHeadUtil.updateSolarPanelAddon(base);
        concealmentChecks();

        //Is there enough power to shoot?
        if ((base.getEnergyLevel(EnumFacing.DOWN) < getPowerRequiredForNextShot())) {
            return;
        }

        //Warn players
        if (base.isAttacksPlayers() && OMTConfig.TURRETS.globalCanTargetPlayers) {
            TurretHeadUtil.warnPlayers(base, base.getWorld(), this.pos, getTurretBaseRange());
        }

        // Update target tracking (Player entity not setting motion data when moving via movement keys)
        if (target instanceof EntityPlayerMP) {
            targetSpeedX = target.posX - targetLastX;
            targetSpeedY = target.posY - targetLastY;
            targetSpeedZ = target.posZ - targetLastZ;
            targetLastX = target.posX;
            targetLastY = target.posY;
            targetLastZ = target.posZ;
        }

        //turret tick rate;
        if (target == null && targetingTicks < OMTConfig.TURRETS.turretTargetSearchTicks) {
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
            if (this.ticks < (this.getTurretBaseFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base, this)))) {
                return;
            }

            // Is there ammo?
            ItemStack ammo = getAmmoStack();
            if (ammo == ItemStack.EMPTY && this.requiresAmmo()) {
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

    protected abstract void doTargetedShot(Entity target, ItemStack ammo);

    protected abstract void doBlindShot(ItemStack ammo);

    public abstract boolean forceShot();

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
            if (chebyshevDistance(target)) {
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
                                          OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
                this.getWorld().setBlockState(this.pos, this.getWorld().getBlockState(pos).withProperty(CONCEALED, true), 3);
            } else {
                ticksWithoutTarget++;
            }

            if (base != null && target != null) {
                ticksWithoutTarget = 0;
                shouldConceal = false;

                if (!playedDeploy) {
                    this.getWorld().playSound(null, this.pos, ModSounds.turretDeploySound, SoundCategory.BLOCKS,
                                              OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
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

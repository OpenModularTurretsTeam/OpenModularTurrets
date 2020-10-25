package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.TargetingSettings;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.api.tileentity.ITurretHead;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.turret.EnumTargetingPriority;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretTargetingUtils;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;

public abstract class TurretHead extends TileEntityBase implements ITurretHead, ITickable, ITurretBaseAddonTileEntity {
    public float baseFitRotationX, baseFitRotationZ;
    public EntityLivingBase target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    protected TurretBase base;
    protected int ticks, targetingTicks, turretTier;
    protected boolean hasSetSide = false;
    protected boolean playedDeploy = false;
    protected boolean autoFire = false;
    protected int ticksWithoutTarget;
    protected double targetLastX = 0;
    protected double targetLastY = 0;
    protected double targetLastZ = 0;
    protected double targetSpeedX = 0;
    protected double targetSpeedY = 0;
    protected double targetSpeedZ = 0;
    protected double cachedAccuracy = 0D;
    protected int cachedScattershot = 0;
    private boolean resetCaches;
    protected ItemStack ammo;
    private EnumFacing turretBase;
    Integer[] priorities = {};

    public TurretHead(int turretTier) {
        this.turretTier = turretTier;
        this.resetCaches = true;
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("ticksBeforeFire", ticks);
        nbtTagCompound.setBoolean("shouldConceal", shouldConceal);
        nbtTagCompound.setBoolean("autoFire", autoFire);
        if (this.priorities.length != 0) {
            nbtTagCompound.setInteger("priorityMaxHP", this.priorities[EnumTargetingPriority.MAX_HP.ordinal()]);
            nbtTagCompound.setInteger("priorityHPRemaining", this.priorities[EnumTargetingPriority.HP_REMAINING.ordinal()]);
            nbtTagCompound.setInteger("priorityDistance", this.priorities[EnumTargetingPriority.DISTANCE.ordinal()]);
            nbtTagCompound.setInteger("priorityArmor", this.priorities[EnumTargetingPriority.ARMOR.ordinal()]);
            nbtTagCompound.setInteger("priorityPlayer", this.priorities[EnumTargetingPriority.PLAYER.ordinal()]);
        }
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.ticks = nbtTagCompound.getInteger("ticksBeforeFire");
        this.shouldConceal = nbtTagCompound.getBoolean("shouldConceal");
        this.autoFire = nbtTagCompound.getBoolean("autoFire");
        if (nbtTagCompound.hasKey("priorityMaxHP")) {
            this.priorities = new Integer[]{
                    nbtTagCompound.getInteger("priorityMaxHP"),
                    nbtTagCompound.getInteger("priorityHPRemaining"),
                    nbtTagCompound.getInteger("priorityDistance"),
                    nbtTagCompound.getInteger("priorityArmor"),
                    nbtTagCompound.getInteger("priorityPlayer")};
        } else {
            this.priorities = this.getDefaultPriorities();
        }
    }

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return base;
    }

    // Set the rotation to fit the turret against the base and store its direction
    protected boolean setSide() {
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

    protected EntityLivingBase getTarget() {
        TurretTargetingUtils selector = new TurretTargetingUtils(this);
        if (!this.getWorld().isRemote && base != null) {
            AxisAlignedBB axis = new AxisAlignedBB(pos.getX() - base.getRange() - 1, pos.getY() - base.getRange() - 1,
                                                   pos.getZ() - base.getRange() - 1, pos.getX() + base.getRange() + 1,
                                                   pos.getY() + base.getRange() + 1, pos.getZ() + base.getRange() + 1);
            List<EntityLivingBase> targets = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);
            return selector.getBestEntity(targets);
        }
        return null;
    }

    public TargetingSettings getTargetingSettings() {
        return base.getTargetingSettings();
    }

    public Integer[] getPriorities() {
        if (priorities.length != 0) {
            return priorities;
        }
        return this.getDefaultPriorities();
    }

    protected TurretBase getBaseFromWorld() {
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
    }

    public TurretBase getBase() {
        return base;
    }

    // If the turret is still on cooldown (from previous shot/activation)
    public boolean isOnCooldown() {
        return (ticks < (this.getTurretBaseFireRate() / (1 + TurretHeadUtil.getFireRateUpgrades(base, this))));
    }

    public boolean getAutoFire() {
        return autoFire;
    }

    public void setAutoFire(boolean autoFire) {
        this.autoFire = autoFire;
    }

    public int getTurretBasePowerUsage() {
        return this.getTurretType().getSettings().powerUsage;
    }

    public int getTurretBaseFireRate() {
        return this.getTurretType().getSettings().baseFireRate;
    }

    public double getBaseTurretAccuracyDeviation() {
        return this.getTurretType().getSettings().baseAccuracyDeviation;
    }

    public int getTurretBaseRange() {
        return this.getTurretType().getSettings().baseRange;
    }

    public double getTurretDamageAmpBonus() {
        return this.getTurretType().getSettings().damageAmp;
    }

    public double getActualTurretAccuracyDeviation() {
        if (this.resetCaches) {
            cachedAccuracy = (this.getBaseTurretAccuracyDeviation())
                    / Math.pow(1 + (TurretHeadUtil.getAccuracyUpgrades(this.getBase(), this)), 1.5D)
                    * (1 + TurretHeadUtil.getScattershotUpgrades(this.getBase()) / 10F);
        }

        return cachedAccuracy;
    }

    public void triggerResetCaches() {
        this.resetCaches = true;
    }

    /**
     * @param entity entity to be checked
     * @return if the entity is valid for the turret to target currently (useful for special turrets)
     */
    public boolean isEntityValidTarget(EntityLivingBase entity) {
        return true;
    }

    /**
     * Priorities determine how much the corresponding priority should prioritise, negative values invert that behaviour.
     *
     * @return priority value array with 5 entries, in the order MAX_HP, HP_REMAINING, DISTANCE, ARMOR, PLAYER
     */
    protected abstract Integer[] getDefaultPriorities();

    protected abstract boolean requiresAmmo();

    protected abstract boolean requiresSpecificAmmo();

    public TurretType getTurretType() {
        BlockAbstractTurretHead block = (BlockAbstractTurretHead) this.getWorld().getBlockState(this.getPos()).getBlock();
        return block.getTurretType();
    }

    @Nullable
    public ItemStack getAmmo() {
        return ammo;
    }

    @Nonnull
    protected abstract SoundEvent getLaunchSoundEffect();

    protected int getPowerRequiredForNextShot() {
        return Math.round(this.getTurretBasePowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));
    }

    protected ItemStack getAmmoStack() {
        ItemStack ammo = ItemStack.EMPTY;
        if (this.requiresAmmo() && OMTConfig.TURRETS.doTurretsNeedAmmo) {
            if (this.requiresSpecificAmmo()) {
                ItemStack ammoNeeded = this.getAmmo();
                if (ammoNeeded != null) {
                    ammoNeeded.setCount(TurretHeadUtil.getScattershotUpgrades(base) + 1);
                }
                ammo = TurretHeadUtil.deductItemStackFromInventories(this.getAmmo(), base, this);
            } else { // for Disposable
                ItemStack ammoNeeded = this.getAmmo();
                if (ammoNeeded != null) {
                    ammoNeeded.setCount(TurretHeadUtil.getScattershotUpgrades(base) + 1);
                }
                ammo = TurretHeadUtil.deductItemStackFromInventories(this.getAmmo(), base, this);
            }
        }
        return ammo;
    }

    public void update() {
        if (!setSide()) return;
        if (this.base == null) {
            this.base = getBaseFromWorld();
        }
        if (this.resetCaches) {
            this.cachedScattershot = TurretHeadUtil.getScattershotUpgrades(this.getBase());
            this.getActualTurretAccuracyDeviation();
            this.resetCaches = false;
        }
    }

    protected boolean updateChecks() {
        ticks++;

        // BASE IS OKAY
        if (base == null || base.getTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
        } else {
            this.concealmentChecks();
            TurretHeadUtil.updateSolarPanelAddon(base);

            //turret tick rate;
            if (target == null && targetingTicks < OMTConfig.TURRETS.turretTargetSearchTicks) {
                targetingTicks++;
                return false;
            }
            targetingTicks = 0;

            int power_required = Math.round(this.getTurretBasePowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                    base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

            // power check
            if ((base.getEnergyStored(EnumFacing.DOWN) < power_required) || (!base.isActive())) {
                return false;
            }

            // is there a target, and Has it died in the previous tick?
            targetingChecks();

            // did we even get a target previously?
            if (target == null) {
                return false;
            }

            // has cooldown passed?
            if (isOnCooldown()) {
                return false;
            }

            // Can the turret still see the target? (It's moving)
            if (target != null) {
                if (!TurretTargetingUtils.canSeeTargetFromPos(this, target)) {
                    target = null;
                    return false;
                }
            }

            if (target != null) {
                if (TurretTargetingUtils.chebyshevDistance(this, target)) {
                    target = null;
                    return false;
                }
            }

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(EnumFacing.DOWN) - power_required, null);
            return true;
        }
        return false;
    }

    protected void targetingChecks() {
        // if no target or target has died, acquire a new target.
        if (this.target == null || this.target.isDead || this.getWorld().getEntityByID(this.target.getEntityId()) == null || this.target.getHealth() <= 0.0F || !TurretTargetingUtils.canSeeTargetFromPos(this, target)) {
            this.target = getTarget();
        }
    }

    void concealmentChecks() {
        if (base != null && base.shouldConcealTurrets) {
            if (!shouldConceal && (target == null || !TurretTargetingUtils.canSeeTargetFromPos(this, target)) && ticksWithoutTarget >= 40) {
                ticksWithoutTarget = 0;
                shouldConceal = true;
                playedDeploy = false;
                this.getWorld().playSound(null, this.pos, ModSounds.turretRetractSound, SoundCategory.BLOCKS,
                                          (float) OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
                this.getWorld().setBlockState(this.pos, this.getWorld().getBlockState(pos).withProperty(CONCEALED, true), 3);
            } else {
                ticksWithoutTarget++;
            }

            if (base != null && target != null) {
                ticksWithoutTarget = 0;
                shouldConceal = false;

                if (!playedDeploy) {
                    this.getWorld().playSound(null, this.pos, ModSounds.turretDeploySound, SoundCategory.BLOCKS,
                                              (float) OMTConfig.TURRETS.turretSoundVolume, new Random().nextFloat() + 0.5F);
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

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
import omtteam.omlib.handler.OMConfig;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.openmodularturrets.api.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.TurretHeadUtil;
import omtteam.openmodularturrets.util.TurretType;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

import static omtteam.omlib.util.player.PlayerUtil.*;
import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;

public abstract class TurretHead extends TileEntityBase implements ITickable, ITurretBaseAddonTileEntity {
    public float baseFitRotationX, baseFitRotationZ;
    public Entity target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    protected TurretBase base;
    protected int ticks, targetingTicks, turretTier;
    protected boolean hasSetSide = false;
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
        nbtTagCompound.setInteger("ticksBeforeFire", ticks);
        nbtTagCompound.setBoolean("shouldConceal", shouldConceal);
        nbtTagCompound.setBoolean("autoFire", autoFire);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.ticks = nbtTagCompound.getInteger("ticksBeforeFire");
        this.shouldConceal = nbtTagCompound.getBoolean("shouldConceal");
        this.autoFire = nbtTagCompound.getBoolean("autoFire");
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
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.east());
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.west()) && this.getWorld().getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.turretBase = EnumFacing.WEST;
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.west());
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.south()) && this.getWorld().getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.turretBase = EnumFacing.SOUTH;
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.south());
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.north()) && this.getWorld().getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.NORTH;
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.north());
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.up()) && this.getWorld().getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.UP;
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.up());
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.down()) && this.getWorld().getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.turretBase = EnumFacing.DOWN;
            this.base = (TurretBase) this.getWorld().getTileEntity(this.pos.down());
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

    public double getBaseTurretAccuracy() {
        return this.getTurretType().getSettings().baseAccuracyDeviation;
    }

    public int getTurretBaseRange() {
        return this.getTurretType().getSettings().baseRange;
    }

    public double getTurretDamageAmpBonus() {
        return this.getTurretType().getSettings().damageAmp;
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

        if (ModCompatibility.ValkyrienWarfareLoaded) {
            // If the turret is in ship space, then move the entity vector into ship space
			// as well before measuring distance
			IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(this.getWorld(),
					this.getPos());
			if (physicsEntity != null) {
				targetPos = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
			}
        }
        if (this.base == null) {
            return false;
        }
        return MathHelper.absMax(MathHelper.absMax(targetPos.x - this.getPos().getX(), targetPos.y - this.getPos().getY()),
                                 targetPos.z - this.getPos().getZ()) > (this.base.getCurrentMaxRange());
    }

    protected int getPowerRequiredForNextShot() {
        return Math.round(this.getTurretBasePowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));
    }

    protected ItemStack getAmmoStack() {
        ItemStack ammo = ItemStack.EMPTY;
        if (this.requiresAmmo()) {
            if (this.requiresSpecificAmmo()) {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getSpecificItemStackFromBase(base, this.getAmmo(), this);
                    if (ammo == ItemStack.EMPTY) {
                        ammo = TurretHeadUtil.getSpecificItemFromInvExpanders(this.getWorld(), this.getAmmo(), base, this);
                    }
                }
            } else {
                for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                    ammo = TurretHeadUtil.getDisposableAmmoFromBase(base, this);
                    if (ammo == ItemStack.EMPTY) {
                        ammo = TurretHeadUtil.getDisposableAmmoFromInvExpander(this.getWorld(), base, this);
                    }
                }
            }
        }
        return ammo;
    }

    protected void targetingChecks() {
        // is there a target, and has it died in the previous tick?
        if (this.target == null || this.target.isDead || this.getWorld().getEntityByID(this.target.getEntityId()) == null || ((EntityLivingBase) this.target).getHealth() <= 0.0F) {
            this.target = getTarget();
        }

        // did we get a target, and is it valid?
        if (this.target != null) {
            //Player checks: is target in creative mode?
            if (this.target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (!base.isAttacksPlayers()
                        || isPlayerOwner(entity, base)
                        || entity.capabilities.isCreativeMode
                        || isPlayerTrusted(entity, base)
                        || isPlayerOP(entity) && OMConfig.GENERAL.canOPAccessOwnedBlocks) {
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

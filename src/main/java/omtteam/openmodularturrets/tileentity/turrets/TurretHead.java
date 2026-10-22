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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.tileentity.TileEntityBase;
import omtteam.omlib.tileentity.TileEntityOwnedBlock;
import omtteam.omlib.util.TargetingSettings;
import omtteam.openmodularturrets.api.tileentity.ITurretBaseAddonTileEntity;
import omtteam.openmodularturrets.api.tileentity.ITurretHead;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.core.base.AmmoDraw;
import omtteam.openmodularturrets.core.base.TurretBaseLogic;
import omtteam.openmodularturrets.core.target.TargetingPriority;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.turret.TurretHeadUtil;
import omtteam.openmodularturrets.turret.TurretTargetingUtils;
import omtteam.openmodularturrets.turret.TurretType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

import static omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead.CONCEALED;

/**
 * A turret. The base picks its target and holds its ammo, energy and upgraded stats ({@link TurretBaseLogic}); the
 * turret aims, keeps its cooldown and fires.
 */
public abstract class TurretHead extends TileEntityBase implements ITurretHead, ITickable, ITurretBaseAddonTileEntity {
    public float baseFitRotationX, baseFitRotationZ;
    public EntityLivingBase target = null;
    public float rotationAnimation = 0.00F;
    public boolean shouldConceal = false;
    protected TurretBase base;
    protected int ticks, turretTier;
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
    /** The 3.x saved priorities; targeting uses the turret type's priorities. */
    Integer[] priorities = {};
    public EnumFacing baseFacing;

    /** @param turretTier the minimum base tier; the core turret type has the same value */
    public TurretHead(int turretTier) {
        this.turretTier = turretTier;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.saveToNBT(var1);
        return new SPacketUpdateTileEntity(this.pos, 2, var1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound var1 = pkt.getNbtCompound();
        loadFromNBT(var1);
    }

    @Override
    public NBTTagCompound saveToNBT(NBTTagCompound nbtTagCompound) {
        super.saveToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("ticksBeforeFire", ticks);
        nbtTagCompound.setBoolean("shouldConceal", shouldConceal);
        nbtTagCompound.setBoolean("autoFire", autoFire);
        if (this.priorities.length != 0) {
            nbtTagCompound.setInteger("priorityMaxHP", this.priorities[TargetingPriority.MAX_HP.ordinal()]);
            nbtTagCompound.setInteger("priorityHPRemaining", this.priorities[TargetingPriority.HP_REMAINING.ordinal()]);
            nbtTagCompound.setInteger("priorityDistance", this.priorities[TargetingPriority.DISTANCE.ordinal()]);
            nbtTagCompound.setInteger("priorityArmor", this.priorities[TargetingPriority.ARMOR.ordinal()]);
            nbtTagCompound.setInteger("priorityPlayer", this.priorities[TargetingPriority.PLAYER.ordinal()]);
        }
        return nbtTagCompound;
    }

    @Override
    public void loadFromNBT(NBTTagCompound nbtTagCompound) {
        super.loadFromNBT(nbtTagCompound);
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
            // kept only for saving; the world is not ready to look up the turret type while loading
            this.priorities = new Integer[0];
        }
    }

    @Nonnull
    @Override
    public TileEntityOwnedBlock getLinkedBlock() {
        return base;
    }

    // Set the rotation to fit the turret against the base and store its direction
    protected boolean setSide() {
        if (hasSetSide && !this.getWorld().isBlockLoaded(this.getPos().offset(baseFacing))) {
            return false;
        }
        if (hasSetSide) {
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.east()) && this.getWorld().getTileEntity(this.pos.east()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.baseFacing = EnumFacing.EAST;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.west()) && this.getWorld().getTileEntity(this.pos.west()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.baseFacing = EnumFacing.WEST;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.south()) && this.getWorld().getTileEntity(this.pos.south()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.baseFacing = EnumFacing.SOUTH;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.north()) && this.getWorld().getTileEntity(this.pos.north()) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.baseFacing = EnumFacing.NORTH;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.up()) && this.getWorld().getTileEntity(this.pos.up()) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.baseFacing = EnumFacing.UP;
            this.hasSetSide = true;
            return true;
        }

        if (this.getWorld().isBlockLoaded(this.pos.down()) && this.getWorld().getTileEntity(this.pos.down()) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.baseFacing = EnumFacing.DOWN;
            this.hasSetSide = true;
            return true;
        }
        return false;
    }

    /** The base face this turret sits on (from the base towards the turret). */
    public EnumFacing getMountFace() {
        return baseFacing.getOpposite();
    }

    /** The game rules of the base; only call while {@link #base} is set. */
    protected TurretBaseLogic logic() {
        return base.getLogic();
    }

    /** The core turret type of this turret. */
    public omtteam.openmodularturrets.core.turret.TurretType getCoreType() {
        return getTurretType().getCore();
    }

    /** The target the base picked for this turret, or null. */
    @Nullable
    protected EntityLivingBase getTargetFromBase() {
        return base != null && baseFacing != null ? base.getTargetEntity(getMountFace()) : null;
    }

    public TargetingSettings getTargetingSettings() {
        return base.getTargetingSettings();
    }

    /** The targeting weights of the turret type (3.x saved them per turret, but they were never changed). */
    public Integer[] getPriorities() {
        int[] weights = getCoreType().getDefaultPriorities();
        Integer[] result = new Integer[weights.length];
        for (int i = 0; i < weights.length; i++) {
            result[i] = weights[i];
        }
        return result;
    }

    protected TurretBase getBaseFromWorld() {
        return TurretHeadUtil.getTurretBase(this.getWorld(), this.pos);
    }

    public TurretBase getBase() {
        return base;
    }

    /** Ticks between shots, with the base's fire rate upgrades. */
    public double getFireCooldownTicks() {
        return logic().getFireCooldownTicks(getCoreType());
    }

    // If the turret is still on cooldown (from previous shot/activation)
    public boolean isOnCooldown() {
        return ticks < getFireCooldownTicks();
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

    /** Range with the base's range upgrades. */
    public int getTurretRange() {
        return logic().getRange(getCoreType());
    }

    public double getTurretDamageAmpBonus() {
        return this.getTurretType().getSettings().damageAmp;
    }

    public double getActualTurretAccuracyDeviation() {
        return logic().getAccuracyDeviation(getCoreType());
    }

    /** Scattershot upgrades that apply to this turret (0 for turrets that ignore them). */
    public int getScattershotCount() {
        return logic().getScattershotCount(getCoreType());
    }

    /** Damage amp addons of the base. */
    public int getDamageAmpLevel() {
        return logic().getDamageAmpLevel();
    }

    /** 3.x fake drops level: -1 without fake drops addon, else the Looting level (0–3). */
    public int getFakeDropsLevel() {
        return logic().getLootingLevel(3);
    }

    /** Shots left in the base: matching ammo items, or the energy for energy turrets. */
    public int getAmmoLevel() {
        return logic().getAmmoLevel(getCoreType());
    }

    /** Nothing is cached any more; the stats are read from the base logic on use. */
    public void triggerResetCaches() {
    }

    public TurretType getTurretType() {
        BlockAbstractTurretHead block = (BlockAbstractTurretHead) this.getWorld().getBlockState(this.getPos()).getBlock();
        return block.getTurretType();
    }

    @Nonnull
    protected abstract SoundEvent getLaunchSoundEffect();

    protected int getPowerRequiredForNextShot() {
        return logic().getPowerPerShot(getCoreType());
    }

    /** Whether this turret fires without ammo (energy turret, ammo disabled or creative base). */
    protected boolean firesWithoutAmmo() {
        return !logic().getRequiredAmmo(getCoreType()).isPresent();
    }

    /**
     * Takes the ammo for one shot from the base: the fired item with the count drawn, or {@link ItemStack#EMPTY} if
     * the turret fires without ammo or the base has not enough.
     */
    protected ItemStack getAmmoStack() {
        if (firesWithoutAmmo()) {
            return ItemStack.EMPTY;
        }
        Optional<AmmoDraw> draw = logic().drawAmmo(getCoreType());
        if (!draw.isPresent() || !(draw.get().item() instanceof ItemStack)) {
            return ItemStack.EMPTY;
        }
        ItemStack ammo = ((ItemStack) draw.get().item()).copy();
        ammo.setCount(draw.get().count());
        return ammo;
    }

    /** Takes the energy of one shot from the base; false if there is not enough. */
    protected boolean consumeShotEnergy() {
        return logic().consumeShotEnergy(getCoreType());
    }

    public void update() {
        if (!setSide()) return;
        if (this.base == null) {
            this.base = getBaseFromWorld();
        }
        // a base whose turret was not loaded when it scanned its neighbours learns about it now
        if (!this.getWorld().isRemote && base != null
                && logic().getTurrets().get(TurretBase.toFace(getMountFace())) != getCoreType()) {
            base.updateNeighbours();
        }
    }

    /** A turret without base, or on a base of a lower tier, breaks. */
    protected boolean hasValidBase() {
        return base != null && !base.isInvalid() && logic().supportsTurret(getCoreType());
    }

    /**
     * Checks of the turrets that do not aim (melee, slowing, teleporter): the base is active, picked a target, the
     * cooldown passed and the energy for the shot was taken.
     *
     * @return whether the turret acts this tick
     */
    protected boolean updateChecks() {
        ticks++;

        if (!hasValidBase()) {
            this.getWorld().destroyBlock(this.pos, true);
            return false;
        }
        if (this.getWorld().isRemote) {
            return false;
        }
        this.concealmentChecks();
        if (!base.isActive()) {
            target = null;
            return false;
        }
        target = getTargetFromBase();
        if (target == null || isOnCooldown()) {
            return false;
        }
        return consumeShotEnergy();
    }

    void concealmentChecks() {
        if (base != null && base.shouldConcealTurrets()) {
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

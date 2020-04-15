package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import omtteam.omlib.util.NetworkUtil;
import omtteam.omlib.util.world.Pos;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.handler.OMTNetworkingHandler;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.network.messages.MessageUpdateTurret;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nullable;

public abstract class AbstractDirectedTurret extends TurretHead {
    protected float pitch, yaw;
    protected float maxPitch = 360;
    protected float maxYaw = 360;
    protected float minPitch = 0;
    protected float minYaw = 0;

    public AbstractDirectedTurret(int turretTier) {
        super(turretTier);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new SPacketUpdateTileEntity(this.getPos(), 2, var1);
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
        nbtTagCompound.setFloat("maxPitch", maxPitch);
        nbtTagCompound.setFloat("minPitch", minPitch);
        nbtTagCompound.setFloat("maxYaw", maxYaw);
        nbtTagCompound.setFloat("minYaw", minYaw);
        return nbtTagCompound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.pitch = nbtTagCompound.getFloat("pitch");
        this.yaw = nbtTagCompound.getFloat("yaw");
        this.maxPitch = nbtTagCompound.getFloat("maxPitch");
        this.minPitch = nbtTagCompound.getFloat("minPitch");
        this.maxYaw = nbtTagCompound.getFloat("maxYaw");
        this.minYaw = nbtTagCompound.getFloat("minYaw");
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

    protected abstract void doTargetedShot(EntityLivingBase target, ItemStack ammo);

    public abstract boolean forceShot();

    protected void updateRotationAnimation() {
        if (rotationAnimation >= 360F) {
            rotationAnimation = 0F;
        }
        rotationAnimation = rotationAnimation + 0.03F;
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
        super.update();

        //Is the turret head block still there?
        if (!(this.getWorld().getBlockState(this.getPos()).getBlock() instanceof BlockAbstractTurretHead)) {
            return;
        }

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

            OMTNetworkingHandler.INSTANCE.sendToAllTracking(
                    new MessageUpdateTurret(this),
                    NetworkUtil.getTargetPointFromBlockPos(this.getWorld().provider.getDimension(), pos, 100));
        }
        this.ticks++;

        //Real time tick updates
        TurretHeadUtil.updateSolarPanelAddon(base);
        concealmentChecks();

        //Is there enough power to shoot?
        if ((base.getEnergyStored(EnumFacing.DOWN) < getPowerRequiredForNextShot())) {
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
                this.yaw = TurretHeadUtil.getAimYaw(this.target, new Pos(this.pos));
                this.pitch = TurretHeadUtil.getAimPitch(this.target, new Pos(this.pos));
            }

            targetingTicks = 0;
        }

        if (target != null && !target.isDead || this.autoFire) {
            // has cooldown passed?
            if (isOnCooldown()) {
                int baseFireRate = this.getTurretBaseFireRate();
                return;
            }

            // Is there ammo?
            ItemStack ammo = getAmmoStack();
            if (ammo == ItemStack.EMPTY && this.requiresAmmo() && OMTConfig.TURRETS.doTurretsNeedAmmo) {
                //TODO: Play some kind of clicking sound?
                return;
            }

            //Finally, try to shoot if criteria is met.
            if (target != null) {
                doTargetedShot(this.target, ammo);
            } else if (this.autoFire) {
                forceShot();
            }

            //If we made it this far, reset ticks to zero
            this.ticks = 0;
        }
    }
}

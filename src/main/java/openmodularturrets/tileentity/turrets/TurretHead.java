package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.ModInfo;
import openmodularturrets.projectiles.TurretProjectile;
import openmodularturrets.tileentity.turretBase.TurretBase;

public abstract class TurretHead extends TileEntity {
    public int ticks;
    public float rotationXY;
    public float rotationXZ;
    public float baseFitRotationX;
    public float baseFitRotationZ;
    public int turretTier;
    public TurretBase base;
    public boolean hasSetSide = false;

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, var1);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    	NBTTagCompound var1 = pkt.func_148857_g();
    	readFromNBT(var1);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        par1.setFloat("rotationXY", rotationXY);
        par1.setFloat("rotationXZ", rotationXZ);
        par1.setInteger("ticksBeforeFire", ticks);
        super.writeToNBT(par1);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        this.rotationXY = par1.getFloat("rotationXY");
        this.rotationXZ = par1.getFloat("rotationXZ");
        this.ticks = par1.getInteger("ticksBeforeFire");
    }

    public void setSide() {
        if (hasSetSide) {
            return;
        }

        if (worldObj.getTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 1.565F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 4.705F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 3.145F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretBase) {
            this.baseFitRotationX = 1.56F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 3.145F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
            return;
        }

        if (worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TurretBase) {
            this.baseFitRotationX = 0F;
            this.baseFitRotationZ = 0F;
            this.hasSetSide = true;
        }
    }

    public Entity getTarget() {
        return TurretHeadUtils.getTarget(
                base,
                worldObj,
                base.getyAxisDetect(),
                xCoord,
                yCoord,
                zCoord,
                getTurretRange() + TurretHeadUtils.getRangeUpgrades(base),
                this
        );
    }

    public abstract int getTurretRange();

    public TurretBase getBase() {
        return TurretHeadUtils.getTurretBase(worldObj, xCoord, yCoord, zCoord);
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

    public float getDistanceToEntity(Entity p_70032_1_)
    {
        float f = (float)(this.xCoord - p_70032_1_.posX);
        float f1 = (float)(this.yCoord - p_70032_1_.posY);
        float f2 = (float)(this.zCoord - p_70032_1_.posZ);
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public abstract int getTurretPowerUsage();

    public abstract int getTurretFireRate();

    public abstract double getTurretAccuracy();

    public abstract boolean requiresAmmo();

    public abstract boolean requiresSpecificAmmo();

    public abstract Item getAmmo();

    public abstract TurretProjectile createProjectile(World world, Entity target, ItemStack ammo);

    public abstract String getLaunchSoundEffect();

    @Override
    public void updateEntity() {

        setSide();
        this.base = getBase();

        if (worldObj.isRemote) {
            return;
        }

        if(ticks%5==0)
        {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getBaseTier() < this.turretTier) {
            this.getWorldObj().func_147480_a(xCoord, yCoord, zCoord, true);
        } else {
            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            int power_required = Math.round(this.getTurretPowerUsage() * (1 - TurretHeadUtils.getEfficiencyUpgrades(base)));

            // power check
            if (!base.isGettingRedstoneSignal() && base.getEnergyStored(ForgeDirection.UNKNOWN) < power_required) {
                return;
            }

            // has cooldown passed?
            if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtils.getFireRateUpgrades(base)))) {
                return;
            }

            Entity target = getTarget();

            // is there a target?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord, yCoord, zCoord);

            ItemStack ammo = null;

            if (this.requiresAmmo()) {
                if (this.requiresSpecificAmmo()) {
                    ammo = TurretHeadUtils.useSpecificItemStackItemFromBase(base, this.getAmmo());
                } else {
                    ammo = TurretHeadUtils.useAnyItemStackFromBase(base);
                }

                // Is there ammo?
                if (ammo == null) {
                    return;
                }
            }

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(ForgeDirection.UNKNOWN) - power_required);

            TurretProjectile projectile = this.createProjectile(this.getWorldObj(), target, ammo);
            projectile.setPosition(this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5);

            if ((projectile.amp_level = TurretHeadUtils.getAmpLevel(base)) != 0) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ModInfo.ID + ":amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY + (double) target.getEyeHeight() - 2.5F - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * (0.2F * (getDistanceToEntity(target) * 0.04F));
            double accuraccy = this.getTurretAccuracy() * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F, (float) accuraccy);

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, ModInfo.ID + ":" + this.getLaunchSoundEffect(), 1.0F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);

            ticks = 0;
        }
    }
}

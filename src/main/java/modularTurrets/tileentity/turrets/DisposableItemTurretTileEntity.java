package modularTurrets.tileentity.turrets;

import modularTurrets.misc.Constants;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;

public class DisposableItemTurretTileEntity extends TurretHead {
    ShootingEntityDisposableItem entity;

    public DisposableItemTurretTileEntity() {
        super();
        this.turretTier = 0;
    }

    public void setSide() {
        if (!hasSetSide) {
            if (worldObj.getTileEntity(xCoord + 1, yCoord, zCoord) instanceof TurretBase) {
                this.baseFitRotationX = 1.56F;
                this.baseFitRotationZ = 1.565F;
                this.hasSetSide = true;
            }

            if (worldObj.getTileEntity(xCoord - 1, yCoord, zCoord) instanceof TurretBase) {
                this.baseFitRotationX = 1.56F;
                this.baseFitRotationZ = 4.705F;
                this.hasSetSide = true;
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord + 1) instanceof TurretBase) {
                this.baseFitRotationX = 1.56F;
                this.baseFitRotationZ = 3.145F;
                this.hasSetSide = true;
            }

            if (worldObj.getTileEntity(xCoord, yCoord, zCoord - 1) instanceof TurretBase) {
                this.baseFitRotationX = 1.56F;
                this.baseFitRotationZ = 0F;
                this.hasSetSide = true;
            }

            if (worldObj.getTileEntity(xCoord, yCoord + 1, zCoord) instanceof TurretBase) {
                this.baseFitRotationX = 3.145F;
                this.baseFitRotationZ = 0F;
                this.hasSetSide = true;
            }

            if (worldObj.getTileEntity(xCoord, yCoord - 1, zCoord) instanceof TurretBase) {
                this.baseFitRotationX = 0F;
                this.baseFitRotationZ = 0F;
                this.hasSetSide = true;
            }
        }
    }

    public ShootingEntityDisposableItem getShootingEntity() {
        if (entity == null) {
            entity = new ShootingEntityDisposableItem(worldObj, null);
            entity.setPosition(this.xCoord + 0.5F, this.yCoord - 1,
                this.zCoord + 0.5F);
        }
        return entity;
    }

    public Entity getTarget() {
        return TurretHeadUtils.getTarget(base, worldObj, base.getyAxisDetect(),
            xCoord, yCoord, zCoord, Constants.disposableItemTurretRange
                + TurretHeadUtils.getRangeUpgrades(base),
            getShootingEntity());
    }

    public TurretBase getBase() {
	return TurretHeadUtils.getTurretBase(worldObj, xCoord, yCoord, zCoord);
    }

    public void loadAmmoIntoEntity() {
	    getShootingEntity().stack = TurretHeadUtils
		    .useAnyItemStackFromBase(base);
    }

    @Override
    public void updateEntity() {

        setSide();
        this.base = getBase();

        if (!worldObj.isRemote) {
            ticks++;

            // UPDATE CLIENTS
            if (!worldObj.isRemote && ticks % 5 == 0) {
                PacketDispatcher.sendPacketToAllPlayers(getDescriptionPacket());
            }

            // BASE IS OKAY
            if (base == null || base.baseTier < this.turretTier) {
                worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
            } else {
                TurretHeadUtils.updateSolarPanelAddon(base);
                TurretHeadUtils.updateRedstoneReactor(base);

                this.target = getTarget();

                // POWER IS OKAY
                if (!base.isGettingRedstoneSignal() && base.storage != null
                    && base.getEnergyStored(ForgeDirection.UNKNOWN) >= Math
                        .round(Constants.disposableItemTurretPowerUse
                                * (1 - TurretHeadUtils
                                .getEfficiencyUpgrades(base)))) {
                    // TICK TO SHOOT BASED ON FIRE RATE
                    if (ticks >= (Constants.disposableItemTurretFireRate * (1 - TurretHeadUtils
                        .getFireRateUpgrades(base)))) {
                        // TARGET IS NOT NULL
                        if (target != null) {

                            this.rotationXZ = TurretHeadUtils.getAimYaw(target,
                                xCoord, yCoord, zCoord) + 3.2F;
                            this.rotationXY = TurretHeadUtils.getAimPitch(
                                target, xCoord, yCoord, zCoord);

                            EntityLivingBase livingBase = (EntityLivingBase) target;
                            loadAmmoIntoEntity();
                            if (entity.stack != null) {
                                base.storage
                                    .setEnergyStored(base.storage
                                            .getEnergyStored()
                                            - (Math.round(Constants.disposableItemTurretPowerUse
                                            * (1 - TurretHeadUtils
                                            .getEfficiencyUpgrades(base)))));

                                getShootingEntity()
                                    .attackEntityWithRangedAttack(
                                            livingBase,
                                            5.5F,
                                            Constants.disposableItemTurretAccurraccy
                                                    * (1 - TurretHeadUtils
                                                    .getAccuraccyUpgrades(base)),
                                            base);
                            }
                            ticks = 0;
                        }
                    }
                }
            }
        }
    }
}

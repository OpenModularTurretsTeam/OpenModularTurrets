package modularTurrets.tileentity.turrets;

import modularTurrets.blocks.Blocks;
import modularTurrets.items.Items;
import modularTurrets.misc.Constants;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class RocketTurretTileEntity extends TurretHead {
    ShootingEntityRocket entity;

    public RocketTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    public ShootingEntityRocket getShootingEntity() {
        if (entity == null) {
            entity = new ShootingEntityRocket(worldObj);
            entity.setPosition(this.xCoord + 0.5F, this.yCoord - 1,
                this.zCoord + 0.5F);
        }

        return entity;
    }

    public Entity getTarget() {
        return TurretHeadUtils.getTarget(base, worldObj, base.getyAxisDetect(),
            xCoord, yCoord, zCoord, Constants.rocketTurretRange
                + TurretHeadUtils.getRangeUpgrades(base),
            getShootingEntity());
    }

    public void loadAmmoIntoEntity() {
        getShootingEntity().stack = TurretHeadUtils
            .useSpecificItemStackItemFromBase(base, Items.rocketCraftable);
    }

    @Override
    public void updateEntity() {
        setSide();
        this.base = getBase();

        if (worldObj.isRemote) {
            return;
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getBaseTier() < this.turretTier) {
            EntityItem turret_item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(Blocks.rocketTurret));
            worldObj.spawnEntityInWorld(turret_item);

            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        } else {
            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            this.target = getTarget();

            // POWER IS OKAY
            if (!base.isGettingRedstoneSignal()
                && base.getEnergyStored(ForgeDirection.UNKNOWN) >= Math
                    .round(Constants.rocketTurretPowerUse
                            * (1 - TurretHeadUtils
                            .getEfficiencyUpgrades(base)))) {
                // TICK TO SHOOT BASED ON FIRE RATE
                if (ticks >= (Constants.rocketTurretFireRate * (1 - TurretHeadUtils
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
                            base
                                .setEnergyStored(base
                                        .getEnergyStored(ForgeDirection.UNKNOWN)
                                        - (Math.round(Constants.rocketTurretPowerUse
                                        * (1 - TurretHeadUtils
                                        .getEfficiencyUpgrades(base)))));
                            getShootingEntity()
                                .attackEntityWithRangedAttack(
                                        livingBase,
                                        5.5F,
                                        Constants.rocketTurretAccurraccy
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

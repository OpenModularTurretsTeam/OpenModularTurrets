package openmodularturrets.tileentity.turrets;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.LaserProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class LaserTurretTileEntity extends TurretHead {

    public LaserTurretTileEntity() {
        super();
        this.turretTier = 4;
    }

    @Override
    public int getTurretRange() {
        return Constants.laserTurretRange;
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
            EntityItem turret_item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(Blocks.laserTurret));
            worldObj.spawnEntityInWorld(turret_item);

            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        } else {

            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            // power check
            if (!base.isGettingRedstoneSignal()
                && base.getEnergyStored(ForgeDirection.UNKNOWN) < Math
                    .round(Constants.laserTurretPowerUse
                            * (1 - TurretHeadUtils
                            .getEfficiencyUpgrades(base)))) {
               return;
            }

            // has cooldown passed?
            if (ticks < (Constants.laserTurretFireRate * (1 - TurretHeadUtils.getFireRateUpgrades(base)))) {
                return;
            }

            target = getTarget();

            // Target found?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target,xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord, yCoord, zCoord);

            base.setEnergyStored(
                    base.getEnergyStored(ForgeDirection.UNKNOWN) -
                            (Math.round(Constants.laserTurretPowerUse *
                                    (1 - TurretHeadUtils.getEfficiencyUpgrades(base)))
                            )
            );

            LaserProjectile projectile = new LaserProjectile(worldObj, this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5, target);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float accuraccy = Constants.laserTurretAccurraccy * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1, d2, 5.0F, accuraccy);

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:laser", 0.5F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);

            ticks = 0;
        }
    }
}

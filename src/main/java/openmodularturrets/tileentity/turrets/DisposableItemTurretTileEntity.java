package openmodularturrets.tileentity.turrets;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.DisposableTurretProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class DisposableItemTurretTileEntity extends TurretHead {
    public DisposableItemTurretTileEntity() {
        super();
        this.turretTier = 0;
    }

    @Override
    public int getTurretRange() {
        return Constants.disposableItemTurretRange;
    }

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
            EntityItem turret_item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(Blocks.disposableItemTurret));
            worldObj.spawnEntityInWorld(turret_item);

            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        } else {
            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            // power check
            if (!base.isGettingRedstoneSignal()
                && base.getEnergyStored(ForgeDirection.UNKNOWN) < Math
                    .round(Constants.disposableItemTurretPowerUse
                            * (1 - TurretHeadUtils
                            .getEfficiencyUpgrades(base)))) {
                return;
            }

            // has cooldown passed?
            if (ticks < (Constants.disposableItemTurretFireRate * (1 - TurretHeadUtils
                .getFireRateUpgrades(base)))) {

                return;
            }

            this.target = getTarget();

            // is there a target?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord, yCoord, zCoord);

            ItemStack ammo = TurretHeadUtils.useAnyItemStackFromBase(base);

            // Is there ammo?
            if (ammo == null) {
                return;
            }

            // Consume energy
            base.setEnergyStored(
                    base.getEnergyStored(ForgeDirection.UNKNOWN) -
                            (Math.round(Constants.disposableItemTurretPowerUse *
                                    (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
                            )
                    )
            );
            
            
            DisposableTurretProjectile projectile = new DisposableTurretProjectile(worldObj, this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5, ammo);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY + (double) target.getEyeHeight() - 2.5F - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * (0.2F * (getDistanceToEntity(target) * 0.04F));
            float accuraccy = Constants.disposableItemTurretAccurraccy * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F, accuraccy);

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:disposable", 1.0F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);
            
            

            ticks = 0;
        }
    }
}

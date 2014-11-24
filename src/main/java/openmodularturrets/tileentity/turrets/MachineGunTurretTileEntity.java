package openmodularturrets.tileentity.turrets;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.BulletProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class MachineGunTurretTileEntity extends TurretHead {
    public MachineGunTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    public int getTurretRange() {
        return Constants.machineGunTurretRange;
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
            EntityItem turret_item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(Blocks.machineGunTurret));
            worldObj.spawnEntityInWorld(turret_item);

            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        } else {

            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            // power check
            if (!base.isGettingRedstoneSignal()
                && base.getEnergyStored(ForgeDirection.UNKNOWN) < Math
                    .round(Constants.machineGunTurretPowerUse
                            * (1 - TurretHeadUtils
                            .getEfficiencyUpgrades(base)))) {
                return;
            }

            // TICK TO SHOOT BASED ON FIRE RATE
            if (ticks < (Constants.machineGunTurretFireRate * (1 - TurretHeadUtils.getFireRateUpgrades(base)))) {
                return;
            }

            target = getTarget();

            // TARGET IS NOT NULL
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord, yCoord, zCoord);

            ItemStack ammo = TurretHeadUtils.useSpecificItemStackItemFromBase(base, Items.bulletCraftable);

            // Is there ammo?
            if (ammo == null) {
                return;
            }

            base.setEnergyStored(
                    base.getEnergyStored(ForgeDirection.UNKNOWN) -
                            (Math.round(Constants.machineGunTurretPowerUse *
                                            (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
                            )
                    )
            );

            BulletProjectile projectile = new BulletProjectile(worldObj, this.xCoord + 0.5, this.yCoord + 1.5, this.zCoord + 0.5);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY + (double) target.getEyeHeight() - 2.5F - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float accuracy = Constants.machineGunTurretAccurraccy * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1, d2, 2.0F, accuracy);

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:machinegun", 1.0F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);

            ticks = 0;
        }
    }
}

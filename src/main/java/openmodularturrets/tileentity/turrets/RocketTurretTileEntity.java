package openmodularturrets.tileentity.turrets;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.RocketProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class RocketTurretTileEntity extends TurretHead {

    public RocketTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    @Override
    public int getTurretRange() {
        return Constants.rocketTurretRange;
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

            // power check
            if (!base.isGettingRedstoneSignal()
                && base.getEnergyStored(ForgeDirection.UNKNOWN) < Math
                    .round(Constants.rocketTurretPowerUse
                            * (1 - TurretHeadUtils
                            .getEfficiencyUpgrades(base)))) {
                return;
            }

            // Has cooldown passed?
            if (ticks < (Constants.rocketTurretFireRate * (1 - TurretHeadUtils
                    .getFireRateUpgrades(base)))) {
                return;
            }

            this.target = getTarget();

            // TARGET IS NOT NULL
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch(target, xCoord, yCoord, zCoord);

            ItemStack ammo = TurretHeadUtils.useSpecificItemStackItemFromBase(base, Items.rocketCraftable);

            // Is there ammo to use?
            if (ammo == null) {
                return;
            }

            base.setEnergyStored(
                    base.getEnergyStored(ForgeDirection.UNKNOWN) -
                            (Math.round(Constants.rocketTurretPowerUse *
                                            (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
                            )
                    )
            );

            RocketProjectile projectile = new RocketProjectile(worldObj, this.xCoord + 0.5, this.yCoord + 1, this.zCoord + 0.5, target);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY + (double) target.getEyeHeight() - 2.5D - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
            float accuracy = Constants.rocketTurretAccurraccy * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F, accuracy);
            projectile.accuracy = accuracy;

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:rocket", 1.0F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);

            ticks = 0;
        }
    }
}

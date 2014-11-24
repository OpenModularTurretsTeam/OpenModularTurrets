package openmodularturrets.tileentity.turrets;

import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.GrenadeProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class GrenadeLauncherTurretTileEntity extends TurretHead {

    public GrenadeLauncherTurretTileEntity() {
        super();
        this.turretTier = 2;
    }

    @Override
    public int getTurretRange() {
        return Constants.grenadeTurretRange;
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
            EntityItem turret_item = new EntityItem(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, new ItemStack(Blocks.grenadeLauncherTurret));
            worldObj.spawnEntityInWorld(turret_item);

            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
        } else {
            TurretHeadUtils.updateSolarPanelAddon(base);
            TurretHeadUtils.updateRedstoneReactor(base);

            // Sufficient power?
            if (!base.isGettingRedstoneSignal() &&
                    base.getEnergyStored(ForgeDirection.UNKNOWN) <
                            Math.round(Constants.grenadeTurretPowerUse *
                                            (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
                            )
                    ) {
                return;
            }

            // has cooldown passed?
            if (ticks < (Constants.grenadeTurretFireRate * (1 - TurretHeadUtils.getFireRateUpgrades(base)))) {
                return;
            }

            this.target = getTarget();

            // Was a target found?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtils.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtils.getAimPitch( target, xCoord, yCoord, zCoord);

            ItemStack ammo = TurretHeadUtils.useSpecificItemStackItemFromBase(base, Items.grenadeCraftable);

            // Is there ammo?
            if (ammo == null) {
                return;
            }

            base.setEnergyStored(
                    base.getEnergyStored(ForgeDirection.UNKNOWN) -
                            (Math.round(Constants.grenadeTurretPowerUse *
                                            (1 - TurretHeadUtils.getEfficiencyUpgrades(base))
                            )
                    )
            );

            GrenadeProjectile projectile = new GrenadeProjectile(worldObj, this.xCoord + 0.5, this.yCoord, this.zCoord + 0.5);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:amped", 1.0F, 1.0F);
                projectile.isAmped = true;
            }

            double d0 = target.posX - this.xCoord;
            double d1 = target.posY - this.yCoord;
            double d2 = target.posZ - this.zCoord;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * (0.2F * (getDistanceToEntity(target) * 0.04F));
            float accuraccy = Constants.grenadeTurretAccurraccy * (1 - TurretHeadUtils.getAccuraccyUpgrades(base));

            projectile.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F, accuraccy);

            this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "openmodularturrets:grenade", 1.0F, 1.0F);
            this.getWorldObj().spawnEntityInWorld(projectile);

            ticks = 0;
        }
    }
}

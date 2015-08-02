package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.util.TurretHeadUtil;

public class RelativisticTurretTileEntity extends TurretHead {
    public RelativisticTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    @Override
    public void updateEntity() {
        setSide();
        this.base = getBase();

        if (worldObj.isRemote) {
            if (rotationAnimation >= 360F) {
                rotationAnimation = 0F;
            }
            rotationAnimation = rotationAnimation + 0.03F;
            return;
        }

        if (ticks % 5 == 0) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getBaseTier() < this.turretTier) {
            this.getWorldObj().func_147480_a(xCoord, yCoord, zCoord, true);
        } else {
            concealmentChecks();
            TurretHeadUtil.updateSolarPanelAddon(base);

            //turret tick rate;
            if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            int power_required = Math.round(this.getTurretPowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                    base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

            // power check
            if ((base.getEnergyStored(ForgeDirection.UNKNOWN) < power_required) || (!base.isActive())) {
                return;
            }

            // is there a target, and Has it died in the previous tick?
            if (target == null || target.isDead || this.getWorldObj().getEntityByID(
                    target.getEntityId()) == null || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTargetWithoutEffect();
            }

            // did we even get a target previously?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtil.getAimYaw(target, xCoord, yCoord, zCoord) + 3.2F;
            this.rotationXY = TurretHeadUtil.getAimPitch(target, xCoord, yCoord, zCoord);

            // has cooldown passed?
            if (ticks < (this.getTurretFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base)))) {
                return;
            }

            // Can the turret still see the target? (It's moving)
            if (target != null) {
                if (!TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) target)) {
                    target = null;
                    return;
                }
            }
            if (target != null && target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (TurretHeadUtil.isTrustedPlayer(entity.getUniqueID(), base)) {
                    target = null;
                    return;
                }
            }
            if (target != null) {
                if (chebyshevDistance(target, base)) {
                    target = null;
                    return;
                }
            }

            ItemStack ammo = null;

            if (this.requiresAmmo()) {
                if (this.requiresSpecificAmmo()) {
                    for (int i = 0; i <= TurretHeadUtil.getScattershotUpgrades(base); i++) {
                        ammo = TurretHeadUtil.useSpecificItemStackItemFromBase(base, this.getAmmo());
                    }
                } else {
                    ammo = TurretHeadUtil.useAnyItemStackFromBase(base);
                }

                // Is there ammo?
                if (ammo == null) {
                    return;
                }
            }

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(ForgeDirection.UNKNOWN) - power_required);

            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.weakness.id, 200));

            target = null;
        }

        this.getWorldObj().playSoundEffect(this.xCoord, this.yCoord, this.zCoord,
                                           ModInfo.ID + ":" + this.getLaunchSoundEffect(), 0.6F, 1.0F);

        ticks = 0;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getRelativistic_turret().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getRelativistic_turret().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getRelativistic_turret().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getRelativistic_turret().getAccuracy();
    }

    @Override
    public boolean requiresAmmo() {
        return false;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return false;
    }

    @Override
    public Item getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return null;
    }

    @Override
    public String getLaunchSoundEffect() {
        return "relativistic";
    }
}

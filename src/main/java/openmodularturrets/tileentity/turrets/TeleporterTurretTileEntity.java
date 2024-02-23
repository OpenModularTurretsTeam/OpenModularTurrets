package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import openmodularturrets.blocks.turretheads.BlockTeleporterTurret;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.reference.ModInfo;
import openmodularturrets.util.TurretHeadUtil;

public class TeleporterTurretTileEntity extends TurretHead {

    public TeleporterTurretTileEntity() {
        super();
        this.turretTier = 4;
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

            // turret tick rate;
            if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            int power_required = Math.round(
                    this.getTurretPowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(base))
                            * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

            // power check
            if ((base.getEnergyStored(ForgeDirection.UNKNOWN) < power_required) || (!base.isActive())) {
                return;
            }

            // is there a target, and has it died in the previous tick?
            if (target == null || target.isDead
                    || this.getWorldObj().getEntityByID(target.getEntityId()) == null
                    || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTargetWithMinRange();
            }

            // did we even get a target previously?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtil.getAimYaw(target, xCoord, zCoord) + 3.2F;
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

            // Consume energy
            base.setEnergyStored(base.getEnergyStored(ForgeDirection.UNKNOWN) - power_required);

            EntityLivingBase base = (EntityLivingBase) target;
            base.setPositionAndUpdate(this.xCoord + 0.5F, this.yCoord + 1.0F, zCoord + 0.5F);

            ((BlockTeleporterTurret) worldObj.getBlock(xCoord, yCoord, zCoord)).shouldAnimate = true;
            target = null;
        }

        this.getWorldObj().playSoundEffect(
                this.xCoord,
                this.yCoord,
                this.zCoord,
                ModInfo.ID + ":" + this.getLaunchSoundEffect(),
                0.6F,
                1.0F);

        ticks = 0;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getTeleporter_turret().getRange();
    }

    @Override
    protected int getTurretPowerUsage() {
        return ConfigHandler.getTeleporter_turret().getPowerUsage();
    }

    @Override
    protected int getTurretFireRate() {
        return ConfigHandler.getTeleporter_turret().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getTeleporter_turret().getAccuracy();
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
    protected String getLaunchSoundEffect() {
        return "teleport";
    }
}

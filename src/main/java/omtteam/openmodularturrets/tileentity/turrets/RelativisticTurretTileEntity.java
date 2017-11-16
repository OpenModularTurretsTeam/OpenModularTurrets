package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import static omtteam.omlib.util.PlayerUtil.isPlayerTrusted;

public class RelativisticTurretTileEntity extends TurretHead {
    public RelativisticTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void update() {
        setSide();
        this.base = getBaseFromWorld();

        if (this.getWorld().isRemote) {
            if (rotationAnimation >= 360F) {
                rotationAnimation = 0F;
            }
            rotationAnimation = rotationAnimation + 0.03F;
            return;
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
        } else {
            concealmentChecks();
            TurretHeadUtil.updateSolarPanelAddon(base);

            //turret tick rate;
            if (target == null && targetingTicks < OMTConfigHandler.getTurretTargetSearchTicks()) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            int power_required = Math.round(this.getTurretPowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                    base)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

            // power check
            if ((base.getEnergyLevel(EnumFacing.DOWN) < power_required) || (!base.isActive())) {
                return;
            }

            // is there a target, and Has it died in the previous tick?
            if (target == null || target.isDead || this.getWorld().getEntityByID(
                    target.getEntityId()) == null || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTargetWithoutEffect();
            }

            // did we even get a target previously?
            if (target == null) {
                return;
            }

            this.yaw = TurretHeadUtil.getAimYaw(target, this.pos) + 3.2F;
            this.pitch = TurretHeadUtil.getAimPitch(target, this.pos);

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

                if (isPlayerTrusted(entity, base)) {
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
            base.setEnergyStored(base.getEnergyLevel(EnumFacing.DOWN) - power_required);
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.getPotionById(2), 200, 5, false, false));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.getPotionById(18), 200, 5, false, false));

            target = null;
        }

        this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);
        ticks = 0;
    }

    @Override
    public int getTurretRange() {
        return OMTConfigHandler.getRelativisticTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfigHandler.getRelativisticTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfigHandler.getRelativisticTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfigHandler.getRelativisticTurretSettings().getAccuracy();
    }


    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfigHandler.getRelativisticTurretSettings().getDamageAmp();
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
    public ItemStack getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return null;
    }

    @Override
    protected SoundEvent getLaunchSoundEffect() {
        return ModSounds.relativisticLaunchSound;
    }
}

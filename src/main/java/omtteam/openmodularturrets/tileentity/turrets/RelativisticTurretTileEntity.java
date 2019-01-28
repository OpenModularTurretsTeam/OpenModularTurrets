package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import javax.annotation.Nonnull;

import static omtteam.omlib.util.player.PlayerUtil.isPlayerTrusted;

public class RelativisticTurretTileEntity extends TurretHead {
    public RelativisticTurretTileEntity() {
        super(3);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void update() {
        if (!setSide()) return;
        if (this.base == null) {
            this.base = getBaseFromWorld();
        }

        ticks++;

        // BASE IS OKAY
        if (base == null || base.getTier() < this.turretTier) {
            this.getWorld().destroyBlock(this.pos, true);
        } else {
            concealmentChecks();
            TurretHeadUtil.updateSolarPanelAddon(base);

            //turret tick rate;
            if (target == null && targetingTicks < OMTConfig.TURRETS.turretTargetSearchTicks) {
                targetingTicks++;
                return;
            }
            targetingTicks = 0;

            int power_required = Math.round(this.getTurretBasePowerUsage() * (1 - TurretHeadUtil.getEfficiencyUpgrades(
                    base, this)) * (1 + TurretHeadUtil.getScattershotUpgrades(base)));

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

            // has cooldown passed?
            if (ticks < (this.getTurretBaseFireRate() * (1 - TurretHeadUtil.getFireRateUpgrades(base, this)))) {
                return;
            }

            // Can the turret still see the target? (It's moving)
            if (target != null) {
                if (!TurretHeadUtil.canTurretSeeTarget(this, (EntityLivingBase) target)) {
                    target = null;
                    return;
                }
            }
            if (target instanceof EntityPlayerMP) {
                EntityPlayerMP entity = (EntityPlayerMP) target;

                if (isPlayerTrusted(entity, base)) {
                    target = null;
                    return;
                }
            }
            if (target != null) {
                if (chebyshevDistance(target)) {
                    target = null;
                    return;
                }
            }

            // Consume energy
            base.setEnergyStored(base.getEnergyLevel(EnumFacing.DOWN) - power_required);
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.getPotionById(2), 200, 3, false, true));
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(Potion.getPotionById(18), 200, 3, false, true));

            target = null;
        }

        this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);
        ticks = 0;
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
    @Nonnull
    protected SoundEvent getLaunchSoundEffect() {
        return ModSounds.relativisticLaunchSound;
    }
}

package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import omtteam.openmodularturrets.blocks.turretheads.BlockTeleporterTurret;
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.compatibility.valkyrienwarfare.ValkyrienWarfareHelper;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.util.TurretHeadUtil;

import static omtteam.omlib.util.PlayerUtil.isPlayerTrusted;

//import omtteam.openmodularturrets.compatability.valkyrienwarfare.ValkyrienWarfareHelper;

public class TeleporterTurretTileEntity extends TurretHead {
    public TeleporterTurretTileEntity() {
        super();
        this.turretTier = 4;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

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
            if (target == null && targetingTicks < ConfigHandler.getTurretTargetSearchTicks()) {
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

            // is there a target, and has it died in the previous tick?
            if (target == null || target.isDead || this.getWorld().getEntityByID(
                    target.getEntityId()) == null || ((EntityLivingBase) target).getHealth() <= 0.0F) {
                target = getTargetWithMinRange();
            }

            // did we even get a target previously?
            if (target == null) {
                return;
            }

            this.rotationXZ = TurretHeadUtil.getAimYaw(target, this.pos) + 3.2F;
            this.rotationXY = TurretHeadUtil.getAimPitch(target, this.pos);

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

            EntityLivingBase base = (EntityLivingBase) target;

            Vec3d basePositionToSet = new Vec3d(this.getPos().getX() + 0.5F, this.getPos().getY() + 1.0F, this.getPos().getZ() + 0.5F);

            if (ModCompatibility.ValkyrienWarfareLoaded) {
                Entity shipEntity = ValkyrienWarfareHelper.getShipManagingBlock(getWorld(), getPos());
                //If not null, then the turret is in ship space, so the coordinates it'll apply to entities must be converter
                // to world coordinates
                if (shipEntity != null) {
                    basePositionToSet = ValkyrienWarfareHelper.getVec3InWorldSpaceFromShipSpace(shipEntity, basePositionToSet);
                }
            }

            base.setPositionAndUpdate(basePositionToSet.xCoord, basePositionToSet.yCoord, basePositionToSet.zCoord);

            ((BlockTeleporterTurret) this.getWorld().getBlockState(this.pos).getBlock()).shouldAnimate = true;
            target = null;
        }

        this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);

        ticks = 0;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getTeleporterTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getTeleporterTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getTeleporterTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getTeleporterTurretSettings().getAccuracy();
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return ConfigHandler.getTeleporterTurretSettings().getDamageAmp();
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
        return ModSounds.teleportLaunchSound;
    }
}

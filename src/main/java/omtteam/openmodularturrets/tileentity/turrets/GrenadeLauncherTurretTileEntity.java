package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.GrenadeProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class GrenadeLauncherTurretTileEntity extends ProjectileTurret {
    public GrenadeLauncherTurretTileEntity() {
        super(3);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfig.TURRETS.laser_turret.getBaseRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfig.TURRETS.laser_turret.getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfig.TURRETS.laser_turret.getBaseFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfig.TURRETS.laser_turret.getBaseAccuracyDeviation() / 10;
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfig.TURRETS.laser_turret.getDamageAmp();
    }

    @Override
    public boolean requiresAmmo() {
        return true;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return true;
    }

    @Override
    public ItemStack getAmmo() {
        return new ItemStack(ModItems.ammoMetaItem, 1, 3);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new GrenadeProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.grenadeLaunchSound;
    }
}

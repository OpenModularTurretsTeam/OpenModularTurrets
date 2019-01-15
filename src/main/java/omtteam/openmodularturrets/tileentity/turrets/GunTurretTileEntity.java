package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.BulletProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class GunTurretTileEntity extends ProjectileTurret {
    public GunTurretTileEntity() {
        super(2);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfig.TURRETS.machine_gun_turret.getBaseRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfig.TURRETS.machine_gun_turret.getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfig.TURRETS.machine_gun_turret.getBaseFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfig.TURRETS.machine_gun_turret.getBaseAccuracyDeviation() / 10;
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfig.TURRETS.machine_gun_turret.getDamageAmp();
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
        return new ItemStack(ModItems.ammoMetaItem, 1, 1);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BulletProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.machinegunLaunchSound;
    }
}

package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;

public class DisposableItemTurretTileEntity extends ProjectileTurret {
    public DisposableItemTurretTileEntity() {
        super(1);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfig.TURRETS.disposable_turret.getBaseRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfig.TURRETS.disposable_turret.getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfig.TURRETS.disposable_turret.getBaseFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfig.TURRETS.disposable_turret.getBaseAccuracyDeviation();
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfig.TURRETS.disposable_turret.getDamageAmp();
    }

    @Override
    public boolean requiresAmmo() {
        return true;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return false;
    }

    @Override
    public ItemStack getAmmo() {
        return ItemStack.EMPTY;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new DisposableTurretProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.disposableLaunchSound;
    }
}

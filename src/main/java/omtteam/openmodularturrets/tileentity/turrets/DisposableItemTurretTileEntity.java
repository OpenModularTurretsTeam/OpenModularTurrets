package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;

public class DisposableItemTurretTileEntity extends TurretHead {
    public DisposableItemTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfigHandler.getDisposableTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfigHandler.getDisposableTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfigHandler.getDisposableTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfigHandler.getDisposableTurretSettings().getAccuracy() / 10;
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfigHandler.getDisposableTurretSettings().getDamageAmp();
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

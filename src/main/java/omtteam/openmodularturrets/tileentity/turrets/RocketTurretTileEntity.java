package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.RocketProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class RocketTurretTileEntity extends TurretHead {
    public RocketTurretTileEntity() {
        super();
        this.turretTier = 4;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfigHandler.getRocketTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfigHandler.getRocketTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfigHandler.getRocketTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfigHandler.getRocketTurretSettings().getAccuracy() / 10;
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfigHandler.getRocketTurretSettings().getDamageAmp();
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
        return new ItemStack(ModItems.ammoMetaItem, 1, 4);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new RocketProjectile(world, target, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.rocketLaunchSound;
    }
}

package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import openmodularturrets.entity.projectiles.LaserProjectile;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;

public class LaserTurretTileEntity extends TurretHead {

    public LaserTurretTileEntity() {
        super();
        this.turretTier = 5;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getLaserTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getLaserTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getLaserTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getLaserTurretSettings().getAccuracy() / 10;
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
        return new LaserProjectile(world, this.getBase());
    }

    @Override
    public String getLaunchSoundEffect() {
        return "laser";
    }
}

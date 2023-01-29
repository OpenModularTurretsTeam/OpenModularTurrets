package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import openmodularturrets.entity.projectiles.FerroSlugProjectile;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.items.Items;

public class RailGunTurretTileEntity extends TurretHead {

    public RailGunTurretTileEntity() {
        super();
        this.turretTier = 5;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getRailgun_turret().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getRailgun_turret().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getRailgun_turret().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getRailgun_turret().getAccuracy() / 10;
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
    public Item getAmmo() {
        return Items.ferroSlug;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new FerroSlugProjectile(world, ammo, this.getBase());
    }

    @Override
    public String getLaunchSoundEffect() {
        return "railGun";
    }
}

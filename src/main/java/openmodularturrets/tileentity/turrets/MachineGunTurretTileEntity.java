package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.items.Items;
import openmodularturrets.misc.ConfigHandler;
import openmodularturrets.projectiles.BulletProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class MachineGunTurretTileEntity extends TurretHead {
    public MachineGunTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getMachineGunTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getMachineGunTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getMachineGunTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getMachineGunTurretSettings().getAccuracy();
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
        return Items.bulletCraftable;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BulletProjectile(world, ammo);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "machinegun";
    }
}

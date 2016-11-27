package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.RocketProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class RocketTurretTileEntity extends TurretHead {
    public RocketTurretTileEntity() {
        super();
        this.turretTier = 4;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getRocketTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getRocketTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getRocketTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getRocketTurretSettings().getAccuracy() / 10;
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
        return new ItemStack(ModItems.ammoMetaItem,1,4);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new RocketProjectile(world, target, ammo, this.getBase());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.rocketLaunchSound;
    }
}

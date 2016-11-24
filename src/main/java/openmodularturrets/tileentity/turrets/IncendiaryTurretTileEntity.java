package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.BlazingClayProjectile;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModItems;
import openmodularturrets.init.ModSounds;

public class IncendiaryTurretTileEntity extends TurretHead {
    public IncendiaryTurretTileEntity() {
        super();
        this.turretTier = 2;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getIncendiary_turret().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getIncendiary_turret().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getIncendiary_turret().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getIncendiary_turret().getAccuracy() / 10;
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
        return ModItems.blazingClayCraftable;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BlazingClayProjectile(world, ammo, this.getBase());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.incendiaryLaunchSound;
    }
}

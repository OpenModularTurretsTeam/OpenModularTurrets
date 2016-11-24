package openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.DisposableTurretProjectile;
import openmodularturrets.entity.projectiles.TurretProjectile;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModSounds;

public class PotatoCannonTurretTileEntity extends TurretHead {
    public PotatoCannonTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    public int getTurretRange() {
        return ConfigHandler.getPotatoCannonTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return ConfigHandler.getPotatoCannonTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return ConfigHandler.getPotatoCannonTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return ConfigHandler.getPotatoCannonTurretSettings().getAccuracy() / 10;
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
        return Items.POTATO;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new DisposableTurretProjectile(world, ammo, this.getBase());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.potatoLaunchSound;
    }
}

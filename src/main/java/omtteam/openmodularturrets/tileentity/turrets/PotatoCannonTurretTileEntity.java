package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.DisposableTurretProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;

public class PotatoCannonTurretTileEntity extends TurretHead {
    public PotatoCannonTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
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
    public double getTurretDamageAmpBonus() {
        return ConfigHandler.getPotatoCannonTurretSettings().getDamageAmp();
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
        return new ItemStack(Items.POTATO);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new DisposableTurretProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.potatoLaunchSound;
    }
}

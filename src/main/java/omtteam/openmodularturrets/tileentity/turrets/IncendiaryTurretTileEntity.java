package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

public class IncendiaryTurretTileEntity extends TurretHead {
    public IncendiaryTurretTileEntity() {
        super();
        this.turretTier = 2;
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

    @Override
    public int getTurretRange() {
        return OMTConfigHandler.getIncendiaryTurretSettings().getRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfigHandler.getIncendiaryTurretSettings().getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfigHandler.getIncendiaryTurretSettings().getFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfigHandler.getIncendiaryTurretSettings().getAccuracy() / 10;
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfigHandler.getIncendiaryTurretSettings().getDamageAmp();
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
        return new ItemStack(ModItems.ammoMetaItem, 1, 0);
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BlazingClayProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.incendiaryLaunchSound;
    }
}

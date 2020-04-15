package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.PlasmaProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class PlasmaLauncherTurretTileEntity extends ProjectileTurret {
    public PlasmaLauncherTurretTileEntity() {
        super(5);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.01F;
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
    public ItemStack getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new PlasmaProjectile(world, this.base);
    }

    @Override
    public Integer[] getDefaultPriorities() {
        return new Integer[]{10, 10, 20, 1, 10};
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.plasmaLaunchSound;
    }
}

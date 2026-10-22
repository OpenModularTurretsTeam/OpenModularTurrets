package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.RocketProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class RocketTurretTileEntity extends ProjectileTurret {
    public RocketTurretTileEntity() {
        super(4);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.00F;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new RocketProjectile(world, target, ammo, this.getBaseFromWorld());
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.rocketLaunchSound;
    }
}

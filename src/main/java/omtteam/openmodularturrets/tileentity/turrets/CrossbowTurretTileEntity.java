package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.ArrowProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class CrossbowTurretTileEntity extends ProjectileTurret {

    public CrossbowTurretTileEntity() {
        super(1);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new ArrowProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.potatoLaunchSound;
    } //Todo: sound effect
}

package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.BlazingClayProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class IncendiaryTurretTileEntity extends ProjectileTurret {
    public IncendiaryTurretTileEntity() {
        super(2);
        ammo = new ItemStack(ModItems.ammoMetaItem, 1, 0);
    }

    @Override
    protected float getProjectileGravity() {
        return 0.03F;
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
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BlazingClayProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public Integer[] getDefaultPriorities() {
        return new Integer[]{10, -5, -2, 10, 10};
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.incendiaryLaunchSound;
    }
}

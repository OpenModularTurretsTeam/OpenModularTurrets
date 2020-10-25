package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import omtteam.openmodularturrets.entity.projectiles.PotatoProjectile;
import omtteam.openmodularturrets.entity.projectiles.TurretProjectile;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class PotatoCannonTurretTileEntity extends ProjectileTurret {

    public PotatoCannonTurretTileEntity() {
        super(1);
        ammo = new ItemStack(Items.POTATO);
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
        return new PotatoProjectile(world, ammo, this.getBaseFromWorld());
    }

    @Override
    public Integer[] getDefaultPriorities() {
        return new Integer[]{-5, -5, -5, 1, 10};
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.potatoLaunchSound;
    }
}

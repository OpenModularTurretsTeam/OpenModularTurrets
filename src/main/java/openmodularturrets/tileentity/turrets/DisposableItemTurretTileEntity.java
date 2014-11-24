package openmodularturrets.tileentity.turrets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.DisposableTurretProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class DisposableItemTurretTileEntity extends TurretHead {
    public DisposableItemTurretTileEntity() {
        super();
        this.turretTier = 0;
    }

    @Override
    public int getTurretRange() {
        return Constants.disposableItemTurretRange;
    }

    @Override
    public Block getTurretBlock() {
        return Blocks.disposableItemTurret;
    }

    @Override
    public int getTurretPowerUsage() {
        return Constants.disposableItemTurretPowerUse;
    }

    @Override
    public int getTurretFireRate() {
        return Constants.disposableItemTurretFireRate;
    }

    @Override
    public float getTurretAccuracy() {
        return Constants.disposableItemTurretAccurraccy;
    }

    @Override
    public boolean requiresAmmo() {
        return true;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return false;
    }

    @Override
    public Item getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new DisposableTurretProjectile(world, ammo);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "disposable";
    }
}

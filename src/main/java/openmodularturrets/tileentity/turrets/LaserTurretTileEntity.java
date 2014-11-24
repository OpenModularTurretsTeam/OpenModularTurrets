package openmodularturrets.tileentity.turrets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.LaserProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class LaserTurretTileEntity extends TurretHead {

    public LaserTurretTileEntity() {
        super();
        this.turretTier = 4;
    }

    @Override
    public int getTurretRange() {
        return Constants.laserTurretRange;
    }

    @Override
    public Block getTurretBlock() {
        return Blocks.laserTurret;
    }

    @Override
    public int getTurretPowerUsage() {
        return Constants.laserTurretPowerUse;
    }

    @Override
    public int getTurretFireRate() {
        return Constants.laserTurretFireRate;
    }

    @Override
    public float getTurretAccuracy() {
        return Constants.laserTurretAccurraccy;
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
    public Item getAmmo() {
        return null;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new LaserProjectile(world);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "laser";
    }
}

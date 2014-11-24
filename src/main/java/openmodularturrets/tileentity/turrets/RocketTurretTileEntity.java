package openmodularturrets.tileentity.turrets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.RocketProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class RocketTurretTileEntity extends TurretHead {

    public RocketTurretTileEntity() {
        super();
        this.turretTier = 3;
    }

    @Override
    public int getTurretRange() {
        return Constants.rocketTurretRange;
    }

    @Override
    public Block getTurretBlock() {
        return Blocks.rocketTurret;
    }

    @Override
    public int getTurretPowerUsage() {
        return Constants.rocketTurretPowerUse;
    }

    @Override
    public int getTurretFireRate() {
        return Constants.rocketTurretFireRate;
    }

    @Override
    public float getTurretAccuracy() {
        return Constants.rocketTurretAccurraccy;
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
        return Items.rocketCraftable;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new RocketProjectile(world, target, ammo);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "rocket";
    }
}

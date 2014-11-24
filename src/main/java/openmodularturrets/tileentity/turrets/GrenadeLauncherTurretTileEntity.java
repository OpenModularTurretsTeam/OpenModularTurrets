package openmodularturrets.tileentity.turrets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.GrenadeProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class GrenadeLauncherTurretTileEntity extends TurretHead {

    public GrenadeLauncherTurretTileEntity() {
        super();
        this.turretTier = 2;
    }

    @Override
    public int getTurretRange() {
        return Constants.grenadeTurretRange;
    }

    @Override
    public Block getTurretBlock() {
        return Blocks.grenadeLauncherTurret;
    }

    @Override
    public int getTurretPowerUsage() {
        return Constants.grenadeTurretPowerUse;
    }

    @Override
    public int getTurretFireRate() {
        return Constants.grenadeTurretFireRate;
    }

    @Override
    public float getTurretAccuracy() {
        return Constants.grenadeTurretAccurraccy;
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
        return Items.grenadeCraftable;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new GrenadeProjectile(world, ammo);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "grenade";
    }
}

package openmodularturrets.tileentity.turrets;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import openmodularturrets.blocks.Blocks;
import openmodularturrets.items.Items;
import openmodularturrets.misc.Constants;
import openmodularturrets.projectiles.BulletProjectile;
import openmodularturrets.projectiles.TurretProjectile;

public class MachineGunTurretTileEntity extends TurretHead {
    public MachineGunTurretTileEntity() {
        super();
        this.turretTier = 1;
    }

    @Override
    public int getTurretRange() {
        return Constants.machineGunTurretRange;
    }

    @Override
    public Block getTurretBlock() {
        return Blocks.machineGunTurret;
    }

    @Override
    public int getTurretPowerUsage() {
        return Constants.machineGunTurretPowerUse;
    }

    @Override
    public int getTurretFireRate() {
        return Constants.machineGunTurretFireRate;
    }

    @Override
    public float getTurretAccuracy() {
        return Constants.machineGunTurretAccurraccy;
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
        return Items.bulletCraftable;
    }

    @Override
    public TurretProjectile createProjectile(World world, Entity target, ItemStack ammo) {
        return new BulletProjectile(world, ammo);
    }

    @Override
    public String getLaunchSoundEffect() {
        return "machinegun";
    }
}

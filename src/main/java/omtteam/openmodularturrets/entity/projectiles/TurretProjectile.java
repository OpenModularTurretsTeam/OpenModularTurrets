package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.tileentity.TurretBase;
import omtteam.openmodularturrets.util.PlayerUtil;
import omtteam.openmodularturrets.util.TurretHeadUtil;

public abstract class TurretProjectile extends EntityThrowable {
    public float gravity;
    boolean isAmped;
    int amp_level;
    ItemStack ammo;
    private TurretBase turretBase;

    TurretProjectile(World world) {
        super(world);
    }

    TurretProjectile(World world, TurretBase turretBase) {
        super(world);
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
    }

    TurretProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world);
        this.ammo = ammo;
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
    }

    boolean canDamagePlayer(EntityPlayer entityPlayer) {
        if (!ConfigHandler.turretDamageTrustedPlayers) {
            if (this.turretBase.getTrustedPlayer(entityPlayer.getUniqueID()) != null || PlayerUtil.getPlayerUIDUnstable(
                    this.turretBase.getOwner()).equals(entityPlayer.getUniqueID())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound nbtTagCompound) {
        this.setDead();
        return false;
    }
}

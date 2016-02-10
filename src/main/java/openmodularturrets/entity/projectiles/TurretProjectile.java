package openmodularturrets.entity.projectiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;
import openmodularturrets.util.TurretHeadUtil;

public abstract class TurretProjectile extends EntityThrowable {
    public float gravity;
    boolean isAmped;
    int amp_level;
    ItemStack ammo;
    private TurretBase turretBase;

    TurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    TurretProjectile(World p_i1776_1_, TurretBase turretBase) {
        super(p_i1776_1_);
        this.turretBase = turretBase;
        if (TurretHeadUtil.getAmpLevel(turretBase) > 0) {
            isAmped = true;
            amp_level = TurretHeadUtil.getAmpLevel(turretBase);
        }
    }

    TurretProjectile(World p_i1776_1_, ItemStack ammo, TurretBase turretBase) {
        super(p_i1776_1_);
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
    public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
        this.setDead();
        return false;
    }
}

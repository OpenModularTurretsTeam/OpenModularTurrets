package openmodularturrets.entity.projectiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;
import openmodularturrets.util.PlayerUtil;

public abstract class TurretProjectile extends EntityThrowable {
    public float gravity;
    protected TurretBase turretBase;

    public TurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public TurretProjectile(World p_i1776_1_, TurretBase turretBase) {
        super(p_i1776_1_);
        this.turretBase = turretBase;
    }

    protected TurretProjectile(World p_i1776_1_, ItemStack ammo, TurretBase turretBase) {
        super(p_i1776_1_);
        this.ammo = ammo;
        this.turretBase = turretBase;
    }

    public boolean isAmped;
    public int amp_level;

    public ItemStack ammo;

    protected boolean canDamagePlayer(EntityPlayer entityPlayer) {
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

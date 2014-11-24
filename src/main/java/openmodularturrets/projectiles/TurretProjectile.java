package openmodularturrets.projectiles;

import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class TurretProjectile extends EntityThrowable {
    public TurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    protected TurretProjectile(World p_i1776_1_, ItemStack ammo) {
        super(p_i1776_1_);
        this.ammo = ammo;
    }

    public boolean isAmped;
    public int amp_level;

    public ItemStack ammo;

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);

        p_70014_1_.setInteger("amp", this.amp_level);

        if (this.ammo == null) {
            return;
        }

        NBTTagCompound ammo_tag = new NBTTagCompound();
        this.ammo.writeToNBT(ammo_tag);

        p_70014_1_.setTag("ammo", ammo_tag);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);

        this.amp_level = p_70037_1_.getInteger("amp");

        if (p_70037_1_.hasKey("ammo")) {
            this.ammo = ItemStack.loadItemStackFromNBT(p_70037_1_.getCompoundTag("ammo"));
        }
    }
}

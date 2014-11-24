package openmodularturrets.projectiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.misc.Constants;

public class DisposableTurretProjectile extends TurretProjectile {
	boolean spawned = false;
	EntityItem itemBound;

    public DisposableTurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public DisposableTurretProjectile(World par1World, ItemStack ammo) {
		super(par1World, ammo);
	}

	@Override
	public void onEntityUpdate() {
		if (!spawned && !this.worldObj.isRemote) {
			itemBound = new EntityItem(this.worldObj, posX, posY, posZ, ammo);

			itemBound.motionX = this.motionX;
			itemBound.motionY = this.motionY;
			itemBound.motionZ = this.motionZ;

            this.worldObj.spawnEntityInWorld(itemBound);
			spawned = true;
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (movingobjectposition.entityHit != null) {
			if (isAmped) {
				movingobjectposition.entityHit.attackEntityFrom(
						DamageSource.generic,
						Constants.disposableItemTurretDamage
								+ Constants.damageAmpDmgBonus);
				movingobjectposition.entityHit.hurtResistantTime = 0;
			} else {
				movingobjectposition.entityHit.attackEntityFrom(
						DamageSource.generic,
						Constants.disposableItemTurretDamage);
				movingobjectposition.entityHit.hurtResistantTime = 0;
			}
		}

		if (itemBound != null) {
			itemBound.setDead();
		}
		this.setDead();
	}

}
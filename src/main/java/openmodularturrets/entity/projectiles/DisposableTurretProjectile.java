package openmodularturrets.entity.projectiles;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;

public class DisposableTurretProjectile extends TurretProjectile {
	boolean spawned = false;
	EntityItem itemBound;

	public DisposableTurretProjectile(World p_i1776_1_) {
		super(p_i1776_1_);
		this.gravity = 0.03F;
	}

	public DisposableTurretProjectile(World par1World, ItemStack ammo) {
		super(par1World, ammo);
		this.gravity = 0.03F;
	}

	@Override
	public void onEntityUpdate() {
		if (!spawned && !this.worldObj.isRemote && ticksExisted >= 2) {
			itemBound = new EntityItem(this.worldObj, posX, posY, posZ, ammo);
			itemBound.motionX = this.motionX;
			itemBound.motionY = this.motionY + 0.1F;
			itemBound.motionZ = this.motionZ;
			itemBound.delayBeforeCanPickup = 100;
			this.worldObj.spawnEntityInWorld(itemBound);
			spawned = true;
		}

		if (ticksExisted > 100) {
			this.setDead();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {

		if (this.ticksExisted <= 2) {
			return;
		}

		if (movingobjectposition.entityHit != null && !worldObj.isRemote) {

			if (movingobjectposition.typeOfHit.equals(0)) {
				if (worldObj.isAirBlock(movingobjectposition.blockX,
						movingobjectposition.blockY,
						movingobjectposition.blockZ)) {
					return;
				}
			}
			
			int damage = ConfigHandler.getDisposableTurretSettings()
					.getDamage();

			if (isAmped) {
				damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
			}

			movingobjectposition.entityHit.attackEntityFrom(
					DamageSource.generic, damage);
			movingobjectposition.entityHit.hurtResistantTime = 0;
		}

		if (itemBound != null) {
			itemBound.setDead();
		}
		this.setDead();
	}

	@Override
	protected float getGravityVelocity() {
		return this.gravity;
	}

}
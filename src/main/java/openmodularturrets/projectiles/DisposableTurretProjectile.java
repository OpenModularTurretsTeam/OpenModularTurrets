package openmodularturrets.projectiles;

import openmodularturrets.misc.Constants;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class DisposableTurretProjectile extends EntityThrowable {
	World world;
	double par2;
	double par4;
	double par6;
	boolean spawned = false;
	ItemStack stack;
	EntityItem itemBound;
	public boolean isAmped = false;

	public DisposableTurretProjectile(World par1World) {
		super(par1World);
	}

	public DisposableTurretProjectile(World par1World,
			EntityLiving par2EntityLiving) {
		super(par1World, par2EntityLiving);
	}

	public DisposableTurretProjectile(World par1World, double par2,
			double par4, double par6, ItemStack stack) {
		super(par1World, par2, par4, par6);
		this.world = par1World;
		this.par2 = par2;
		this.par4 = par4;
		this.par6 = par6;
		this.stack = stack;

	}

	@Override
	public void onEntityUpdate() {
		if (!spawned && !world.isRemote) {
			itemBound = new EntityItem(world, par2, par4, par6, stack);

			itemBound.motionX = this.motionX;
			itemBound.motionY = this.motionY;
			itemBound.motionZ = this.motionZ;

			world.spawnEntityInWorld(itemBound);
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
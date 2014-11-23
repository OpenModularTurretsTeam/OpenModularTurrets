package modularTurrets.projectiles;

import modularTurrets.misc.Constants;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BulletProjectile extends EntityThrowable {

	private int ticksAlive;
	public boolean isAmped = false;

	public BulletProjectile(World par1World) {
		super(par1World);
	}

	public BulletProjectile(World par1World, double par2, double par4,
			double par6) {
		super(par1World, par2, par4, par6);

	}

	@Override
	public void onEntityUpdate() {

		if (ticksAlive >= 50) {
			this.setDead();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		// TODO Auto-generated method stub
		if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
			if (isAmped) {
				movingobjectposition.entityHit.attackEntityFrom(
						DamageSource.generic, Constants.machineGunTurretDamage
								+ Constants.damageAmpDmgBonus);
				movingobjectposition.entityHit.hurtResistantTime = 0;
			} else {
				movingobjectposition.entityHit.attackEntityFrom(
						DamageSource.generic, Constants.machineGunTurretDamage);
				movingobjectposition.entityHit.hurtResistantTime = 0;
			}

		}

		if (movingobjectposition.entityHit == null && !worldObj.isRemote) {
			worldObj.playSoundEffect(posX, posY, posZ,
					"modularturrets:bulletHit", 1.0F, 1.0F);
		}
		this.setDead();
	}

	@Override
	protected void updateFallState(double par1, boolean par3) {
		this.posY = posY + 12F;
	}

	@Override
	protected float getGravityVelocity() {
		return 0.00F;
	}
}
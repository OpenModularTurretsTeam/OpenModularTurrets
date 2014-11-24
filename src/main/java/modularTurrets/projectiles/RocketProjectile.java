package modularTurrets.projectiles;

import java.util.List;
import java.util.Random;

import modularTurrets.misc.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class RocketProjectile extends EntityThrowable {

	private int ticksAlive = 0;
	private Entity entity;
	public float speed = 0.1F;
	public int upwardsFirst = 15;
	public float yaw;
	public int arrowShake;
	public float accuracy;
	public boolean isAmped = false;

	public RocketProjectile(World par1World) {
		super(par1World);
	}

	public RocketProjectile(World par1World, double par2, double par4, double par6, Entity entity) {
		super(par1World, par2, par4, par6);
		this.entity = entity;
		posY = posY - 0.2;
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
		this.setDead();
	}

	@Override
	public void onEntityUpdate() {

		if (entity != null) {
			double d0 = entity.posX - this.posX;
			double d1 = entity.posY + (double) entity.getEyeHeight() - 1.1F - this.posY;
			double d2 = entity.posZ - this.posZ;

			if (ticksAlive >= upwardsFirst) {
				this.setThrowableHeading(d0, d1, d2, speed, 0.0F);
			} else {
				this.setThrowableHeading(d0 / 5, 2.0F, d2 / 5, speed, accuracy);
				speed = speed + 0.3F;
			}

			double dX = (entity.posX) - (this.posX);
			double dZ = (entity.posZ) - (this.posZ);
			yaw = ((float) (Math.atan2(dZ, dX))) - 1.570796F;

		}

		ticksAlive++;

		if (ticksAlive >= 100) {
			this.setDead();
		}

		for (int i = 0; i <= 20; i++) {
			Random random = new Random();
			worldObj.spawnParticle("flame",
					posX + (random.nextGaussian() / 10),
					posY + (random.nextGaussian() / 10),
					posZ + (random.nextGaussian() / 10), (motionX * -1),
					(motionY * -1), (motionZ * -1));
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
		if (!worldObj.isRemote) {
			worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
			AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.posX - 3,
					this.posY - 5, this.posZ - 5, this.posX + 5, this.posY + 5,
					this.posZ + 5);

			List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class, axis);

			for (Entity mob : targets) {
				if (isAmped) {
					mob.attackEntityFrom(DamageSource.generic, 10 + Constants.damageAmpDmgBonus);
					mob.hurtResistantTime = 0;
				} else {
					mob.attackEntityFrom(DamageSource.generic, 10);
					mob.hurtResistantTime = 0;
				}
			}
		}
		this.setDead();
	}

	@Override
	protected float getGravityVelocity() {
		return 0.00F;
	}
}
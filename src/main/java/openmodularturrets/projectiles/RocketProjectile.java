package openmodularturrets.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.misc.ConfigHandler;

import java.util.Random;

public class RocketProjectile extends TurretProjectile {

	private int ticksAlive = 0;
	private Entity target;
	public float speed = 0.1F;
	public int upwardsFirst = 15;
	public float yaw;
	public int arrowShake;
	public float accuracy;

    public RocketProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public RocketProjectile(World par1World, Entity target, ItemStack ammo) {
		super(par1World, ammo);

        this.target = target;
	}

    @Override
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
		this.setDead();
	}

	@Override
	public void onEntityUpdate() {

		if (target != null) {
			double d0 = target.posX - this.posX;
			double d1 = target.posY + (double) target.getEyeHeight() - 1.1F - this.posY;
			double d2 = target.posZ - this.posZ;

			if (ticksAlive >= upwardsFirst) {
				this.setThrowableHeading(d0, d1, d2, speed, 0.0F);
			} else {
				this.setThrowableHeading(d0 / 5, 2.0F, d2 / 5, speed, accuracy);
				speed = speed + 0.3F;
			}

			double dX = (target.posX) - (this.posX);
			double dZ = (target.posZ) - (this.posZ);
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

            int damage =  ConfigHandler.getRocketTurretSettings().getDamage();

            if (isAmped) {
                damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
            }

            movingobjectposition.entityHit.attackEntityFrom(DamageSource.generic, damage);
            movingobjectposition.entityHit.hurtResistantTime = 0;
		}
		this.setDead();
	}

	@Override
	protected float getGravityVelocity() {
		return 0.00F;
	}
}
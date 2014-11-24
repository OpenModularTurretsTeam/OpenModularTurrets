package openmodularturrets.projectiles;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.misc.Constants;

public class BulletProjectile extends TurretProjectile {

    public BulletProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
    }

    public BulletProjectile(World par1World, ItemStack ammo) {
		super(par1World, ammo);
	}

    @Override
	public void onEntityUpdate() {
		if (ticksExisted >= 50) {
			this.setDead();
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {
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
					"openmodularturrets:bulletHit", 1.0F, 1.0F);
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
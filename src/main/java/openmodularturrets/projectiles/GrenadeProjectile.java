package openmodularturrets.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.misc.Constants;

import java.util.List;

public class GrenadeProjectile extends TurretProjectile {
	public boolean isAmped;

	public GrenadeProjectile(World world) {
		super(world);
	}

    public GrenadeProjectile(World world, ItemStack ammo) {
        super(world, ammo);
    }

    @Override
	public void onEntityUpdate() {
		if (ticksExisted >= 100) {
			this.setDead();
		}

		for (int i = 0; i <= 20; i++) {
			worldObj.spawnParticle("reddust", posX, posY, posZ, 1.0D, 1.0D, 1.0D);
		}
	}

	@Override
	protected void onImpact(MovingObjectPosition movingobjectposition) {

		worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
		AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.posX - 3,
				this.posY - 3, this.posZ - 3, this.posX + 3, this.posY + 3,
				this.posZ + 3);
		List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class, axis);

		for (Entity mob : targets) {
			if (isAmped) {
				mob.attackEntityFrom(DamageSource.generic,
						Constants.grenadeTurretDamage
								+ Constants.damageAmpDmgBonus);
				mob.hurtResistantTime = 0;
			} else {
				mob.attackEntityFrom(DamageSource.generic,
						Constants.grenadeTurretDamage);
				mob.hurtResistantTime = 0;
			}
		}

		this.setDead();

	}

}
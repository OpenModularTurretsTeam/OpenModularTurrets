package openmodularturrets.entity.projectiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.handler.ConfigHandler;
public class LaserProjectile extends TurretProjectile {
    public int arrowShake;

    public LaserProjectile(World par1World) {
	    super(par1World);
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
	this.setDead();
    }

    @Override
    public void onEntityUpdate() {
        this.posY = posY + (fallDistance * -1);

        if (ticksExisted >= 50) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {
        worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:laserHit", 1.0F, 1.0F);

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            int damage =  ConfigHandler.getLaserTurretSettings().getDamage();

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
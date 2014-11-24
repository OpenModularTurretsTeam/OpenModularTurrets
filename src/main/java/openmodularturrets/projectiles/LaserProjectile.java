package openmodularturrets.projectiles;

import openmodularturrets.misc.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class LaserProjectile extends EntityThrowable {
    public int arrowShake;
    public boolean isAmped = false;

    public LaserProjectile(World par1World) {
	super(par1World);
    }

    public LaserProjectile(World par1World, double par2, double par4, double par6, Entity entity) {
        super(par1World, par2, par4, par6);
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
            if (isAmped) {
                movingobjectposition.entityHit.attackEntityFrom(
                    DamageSource.generic, Constants.laserTurretDamage + Constants.damageAmpDmgBonus);
                movingobjectposition.entityHit.hurtResistantTime = 0;
            } else {
                movingobjectposition.entityHit.attackEntityFrom(
                    DamageSource.generic, Constants.laserTurretDamage);
                movingobjectposition.entityHit.hurtResistantTime = 0;
            }
        }
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
	return 0.00F;
    }

}
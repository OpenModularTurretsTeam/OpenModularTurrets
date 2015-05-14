package openmodularturrets.entity.projectiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class LaserProjectile extends TurretProjectile {
    public int arrowShake;

    public LaserProjectile(World par1World, TurretBase turretBase) {
        super(par1World);
        this.gravity = 0.00F;
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
        this.setDead();
        this.gravity = 0.00F;
    }

    @Override
    public void onEntityUpdate() {

        if (ticksExisted >= 50) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {

        if (this.ticksExisted <= 1) {
            return;
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {

            if (movingobjectposition.typeOfHit.equals(0)) {
                if (worldObj.isAirBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                        movingobjectposition.blockZ)) {
                    return;
                }
            }

            worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:laserHit", 1.0F, 1.0F);

            if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
                int damage = ConfigHandler.getLaserTurretSettings().getDamage();
                if (isAmped) {
                    damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
                }
                if (movingobjectposition.entityHit instanceof EntityPlayer) {
                    if (canDamagePlayer((EntityPlayer) movingobjectposition.entityHit)) {
                        movingobjectposition.entityHit.setFire(2);
                        movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("laser"), damage);
                        movingobjectposition.entityHit.hurtResistantTime = 0;
                    }
                } else {
                    movingobjectposition.entityHit.setFire(2);
                    movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("laser"), damage);
                    movingobjectposition.entityHit.hurtResistantTime = 0;
                }
            }
        }
        this.setDead();
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
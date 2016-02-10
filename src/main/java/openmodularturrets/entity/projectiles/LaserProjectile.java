package openmodularturrets.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.Random;

public class LaserProjectile extends TurretProjectile {
    public int arrowShake;

    public LaserProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }

    public LaserProjectile(World par1World, TurretBase turretBase) {
        super(par1World, turretBase);
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
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            Block hitBlock = worldObj.getBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                               movingobjectposition.blockZ);
            if (hitBlock != null && !hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            Random random = new Random();
            worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:laserHit",
                                     ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);

            if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
                int damage = ConfigHandler.getLaserTurretSettings().getDamage();

                if (isAmped) {
                    if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                        damage += ((int) elb.getHealth() * (0.1 * amp_level));
                    }
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
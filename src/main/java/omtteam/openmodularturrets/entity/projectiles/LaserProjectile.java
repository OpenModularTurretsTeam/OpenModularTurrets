package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class LaserProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public int arrowShake;

    @SuppressWarnings("unused")
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
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult movingobjectposition) {
        if (this.ticksExisted <= 1) {
            return;
        }
        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState hitBlock = worldObj.getBlockState(movingobjectposition.getBlockPos());

            if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
                return;
            }

            if (!hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            Random random = new Random();
            worldObj.playSound(null, new BlockPos(posX, posY, posZ), ModSounds.laserHitSound, SoundCategory.AMBIENT,
                    ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);

            if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
                int damage = ConfigHandler.getLaserTurretSettings().getDamage();

                if (isAmped) {
                    if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                        damage += ((int) elb.getHealth() * (0.06F * amp_level));
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
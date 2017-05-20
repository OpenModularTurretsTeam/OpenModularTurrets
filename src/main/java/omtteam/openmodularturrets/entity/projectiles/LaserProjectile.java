package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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

    @ParametersAreNonnullByDefault
    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {

        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid()) {
            // Go through non solid block
            return;
        }

        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote) {
            Random random = new Random();
            getEntityWorld().playSound(null, new BlockPos(posX, posY, posZ), ModSounds.laserHitSound, SoundCategory.AMBIENT,
                    ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);

            int damage = ConfigHandler.getLaserTurretSettings().getDamage();

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (0.06F * amp_level));
                }
            }

            if (entity instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) entity)) {
                    entity.setFire(2);
                    entity.attackEntityFrom(new NormalDamageSource("laser"), damage);
                    entity.hurtResistantTime = 0;
                }
            } else {
                entity.setFire(2);
                entity.attackEntityFrom(new NormalDamageSource("laser"), damage);
                entity.hurtResistantTime = 0;
                setMobDropLoot(entity);
            }
        }
        this.setDead();
    }


    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
    }
}
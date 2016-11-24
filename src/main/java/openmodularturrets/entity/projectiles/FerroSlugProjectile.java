package openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.init.ModSounds;
import openmodularturrets.tileentity.TurretBase;

import java.util.Random;

public class FerroSlugProjectile extends TurretProjectile {
    public FerroSlugProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public FerroSlugProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.00F;
    }

    @Override
    public void onEntityUpdate() {
        if (ticksExisted >= 50) {
            this.setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult movingobjectposition) {
        if (this.ticksExisted <= 1) {
            return;
        }
        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState hitBlock = worldObj.getBlockState(movingobjectposition.getBlockPos());
            if (hitBlock != null && !hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            if (movingobjectposition.typeOfHit.equals(0)) {
                if (worldObj.isAirBlock(movingobjectposition.getBlockPos())) {
                    return;
                }
            }

            int damage = ConfigHandler.getRailgun_turret().getDamage();

            if (isAmped) {
                if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                    damage += ((int) elb.getHealth() * (0.25 * amp_level));
                }
            }

            Random random = new Random();
            worldObj.playSound(null, new BlockPos(posX, posY, posZ), ModSounds.railGunHitSound, SoundCategory.AMBIENT,
                    ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);

            if (movingobjectposition.entityHit instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) movingobjectposition.entityHit)) {
                    movingobjectposition.entityHit.attackEntityFrom(new ArmorBypassDamageSource("ferroslug"), damage);
                    movingobjectposition.entityHit.hurtResistantTime = 0;
                }
            } else {
                movingobjectposition.entityHit.attackEntityFrom(new ArmorBypassDamageSource("ferroslug"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;
            }
        }

        this.setDead();
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        this.posY = posY + 12F;
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import omtteam.omlib.util.RandomUtil;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class BulletProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public BulletProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public BulletProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.00F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {

        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid()) {
            // Go through non solid block
            return;
        }

        playSound();
        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {

        if (entity != null && !getEntityWorld().isRemote) {

            int damage = ConfigHandler.getGunTurretSettings().getDamage();

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (0.06F * amp_level));
                }
            }

            if (entity instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) entity)) {
                    entity.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                    entity.hurtResistantTime = 0;
                    playSound();
                } else {
                    return;
                }
            } else {
                entity.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                entity.hurtResistantTime = 0;

            }
            setMobDropLoot(entity);
            this.setDead();
        }
    }

    @Override
    public void playSound() {
        Random random = RandomUtil.random;
        getEntityWorld().playSound(null, new BlockPos(posX, posY, posZ), ModSounds.bulletHitSound, SoundCategory.AMBIENT,
                ConfigHandler.getTurretSoundVolume(), random.nextFloat() + 0.5F);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
        this.posY = posY + 12F;
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }

    @Override
    protected void onImpact(RayTraceResult result) {
    }
}
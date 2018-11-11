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
import net.minecraft.world.WorldServer;
import omtteam.omlib.util.EntityUtil;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

public class FerroSlugProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public FerroSlugProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public FerroSlugProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
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
        } else if (OMTConfig.TURRETS.canRailgunDestroyBlocks) {
            getEntityWorld().destroyBlock(pos, false);
        }

        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {

            int damage = OMTConfig.TURRETS.railgun_turret.getBaseDamage();

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getDamageAmpBonus() * amp_level));
                }
            }

            damage *= EntityUtil.getEntityArmor(entity) / 20F - 0.5F;

            if (entity instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) entity)) {
                    entity.attackEntityFrom(new ArmorBypassDamageSource("ferroslug", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
                    entity.hurtResistantTime = -1;
                    playSound();
                } else {
                    return;
                }
            } else if (canDamageEntity(entity)) {
                setTagsForTurretHit(entity);
                entity.attackEntityFrom(new ArmorBypassDamageSource("ferroslug", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
                entity.hurtResistantTime = -1;
                playSound();
            } else {
                return;
            }

            this.setDead();
        }
    }

    @Override
    public void playSound() {
        Random random = new Random();
        getEntityWorld().playSound(null, new BlockPos(posX, posY, posZ), ModSounds.railGunHitSound, SoundCategory.AMBIENT,
                                   OMTConfig.TURRETS.turretSoundVolume, random.nextFloat() + 0.5F);
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
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult result) {
    }

    @Override
    public double getDamageAmpBonus() {
        return OMTConfig.TURRETS.railgun_turret.getDamageAmp();
    }
}
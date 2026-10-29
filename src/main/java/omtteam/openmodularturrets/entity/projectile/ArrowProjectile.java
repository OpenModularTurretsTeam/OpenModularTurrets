package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Crossbow turret bolt (shown as an arrow). The class and its entity id {@code arrowProjectile} stay from the time the
 * crossbow turret fired vanilla arrows.
 */
public class ArrowProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public ArrowProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.03F;
    }

    public ArrowProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.03F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onHitBlock(IBlockState hitBlock, BlockPos pos) {
        if (hitBlock.getBlock() instanceof BlockTurret) {
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
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile) && !this.isDead) {
            int damage = OMTConfig.TURRETS.crossbow_turret.baseDamage;

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getDamageAmpBonus() * amp_level));
                }
            }

            if (canDamageEntity(entity)) {
                if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                entity.attackEntityFrom(new NormalDamageSource("arrow", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true, entity), damage);
                if (damage > 0) notifyHit(entity);
                entity.hurtResistantTime = -1;
            } else {
                return;
            }

            this.setDead();
        }
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
    public void playSound() {

    }

    @Override
    public double getDamageAmpBonus() {
        return OMTConfig.TURRETS.crossbow_turret.damageAmp;
    }
}
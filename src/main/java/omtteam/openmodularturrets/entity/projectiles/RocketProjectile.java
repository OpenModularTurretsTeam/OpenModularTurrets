package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Random;

public class RocketProjectile extends TurretProjectile {
    @SuppressWarnings("unused")
    public int arrowShake;
    private Entity target;

    @SuppressWarnings("unused")
    public RocketProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }

    @SuppressWarnings("unused")
    public RocketProjectile(World p_i1776_1_, TurretBase turretBase) {
        super(p_i1776_1_, turretBase);
        this.gravity = 0.00F;
    }

    public RocketProjectile(World par1World, Entity target, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.00F;
        this.target = target;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

        if (!getEntityWorld().isRemote) {
            if (OMTConfig.TURRETS.canRocketsHome && target != null) {
                double d0 = target.posX - this.posX;
                double d1 = target.posY + (double) target.getEyeHeight() - 1.1F - this.posY;
                double d2 = target.posZ - this.posZ;

                float speed = 0.24F;
                this.shoot(d0, d1, d2, speed, 0.0F);
            } else if (OMTConfig.TURRETS.canRocketsHome) {
                this.setDead();
            }
        }

        for (int i = 0; i <= 20; i++) {
            Random random = new Random();
            getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (random.nextGaussian() / 10), posY + (random.nextGaussian() / 10),
                                           posZ + (random.nextGaussian() / 10), (0), (0), (0));
        }
    }

    private void explode() {
        if (!getEntityWorld().isRemote) {
            float strength = OMTConfig.TURRETS.canRocketsDestroyBlocks ? 2.3F : 0.1F;
            getEntityWorld().createExplosion(null, posX, posY, posZ, strength, true);
            AxisAlignedBB axis = new AxisAlignedBB(this.posX - 5, this.posY - 5, this.posZ - 5,
                                                   this.posX + 5, this.posY + 5, this.posZ + 5);
            List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase entity : targets) {
                int damage = OMTConfig.TURRETS.rocket_turret.baseDamage;

                if (isAmped) {
                    damage += ((int) entity.getHealth() * (getDamageAmpBonus() * amp_level));
                }

                if (OMTConfig.TURRETS.canRocketsHurtEnderDragon && entity instanceof EntityDragon) {
                    setTagsForTurretHit(entity);
                    (entity).setHealth((entity).getHealth() - damage);
                    entity.hurtResistantTime = -1;
                } else if (canDamageEntity(entity)) {
                    if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                    entity.attackEntityFrom(new NormalDamageSource("rocket", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true), damage);
                    entity.hurtResistantTime = -1;
                }
            }
        }
        this.setDead();
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

        explode();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (!getEntityWorld().isRemote && !(entity instanceof EntityPlayer && !canDamagePlayer((EntityPlayer) entity))
                && canDamageEntity(entity) && !(entity instanceof TurretProjectile)) {
            explode();
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
        return OMTConfig.TURRETS.rocket_turret.damageAmp;
    }
}

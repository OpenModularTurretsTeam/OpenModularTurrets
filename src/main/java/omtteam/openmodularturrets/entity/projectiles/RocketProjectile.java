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
import omtteam.openmodularturrets.handler.OMTConfigHandler;
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
            if (OMTConfigHandler.canRocketsHome && target != null) {
                double d0 = target.posX - this.posX;
                double d1 = target.posY + (double) target.getEyeHeight() - 1.1F - this.posY;
                double d2 = target.posZ - this.posZ;

                float speed = 0.24F;
                this.setThrowableHeading(d0, d1, d2, speed, 0.0F);
            } else if (OMTConfigHandler.canRocketsHome && target == null) {
                this.setDead();
            }
        }

        for (int i = 0; i <= 20; i++) {
            Random random = new Random();
            getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (random.nextGaussian() / 10), posY + (random.nextGaussian() / 10),
                    posZ + (random.nextGaussian() / 10), (0), (0), (0));
        }
    }

    private void damageEntityLivingBase(EntityLivingBase mob) {
        int damage = OMTConfigHandler.getRocketTurretSettings().getDamage();

        if (isAmped) {
            damage += ((int) mob.getHealth() * (getDamageAmpBonus() * amp_level));
        }


        if (mob instanceof EntityPlayer) {
            if (canDamagePlayer((EntityPlayer) mob)) {
                mob.attackEntityFrom(new NormalDamageSource("rocket", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
                mob.hurtResistantTime = 0;
            }
        }

        if (OMTConfigHandler.isCanRocketsHurtEnderDragon() && mob instanceof EntityDragon) {
            (mob).setHealth((mob).getHealth() - damage);
            mob.hurtResistantTime = 0;
        } else if (canDamageEntity(mob)) {
            mob.attackEntityFrom(new NormalDamageSource("rocket", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
            mob.hurtResistantTime = 0;
        }
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

        if (!getEntityWorld().isRemote) {
            float strength = OMTConfigHandler.canRocketsDestroyBlocks ? 2.3F : 0.1F;
            getEntityWorld().createExplosion(null, posX, posY, posZ, strength, true);
            AxisAlignedBB axis = new AxisAlignedBB(this.posX - 5, this.posY - 5, this.posZ - 5,
                    this.posX + 5, this.posY + 5, this.posZ + 5);
            List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase mob : targets) {
                damageEntityLivingBase(mob);
                setTagsForTurretHit(mob);
            }
        }
        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (!getEntityWorld().isRemote && !(entity instanceof EntityPlayer && !canDamagePlayer((EntityPlayer) entity))
                && canDamageEntity(entity) && !(entity instanceof TurretProjectile)) {
            float strength = OMTConfigHandler.canRocketsDestroyBlocks ? 2.3F : 0.1F;
            getEntityWorld().createExplosion(null, posX, posY, posZ, strength, true);
            AxisAlignedBB axis = new AxisAlignedBB(this.posX - 5, this.posY - 5, this.posZ - 5,
                    this.posX + 5, this.posY + 5, this.posZ + 5);
            List<EntityLivingBase> targets = getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (EntityLivingBase mob : targets) {
                damageEntityLivingBase(mob);
                setTagsForTurretHit(mob);
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
        return OMTConfigHandler.getRocketTurretSettings().getDamageAmp();
    }
}

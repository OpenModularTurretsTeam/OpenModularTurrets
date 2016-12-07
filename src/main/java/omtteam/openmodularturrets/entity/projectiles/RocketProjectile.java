package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import omtteam.openmodularturrets.blocks.BlockTurretBase;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.ConfigHandler;
import omtteam.openmodularturrets.tileentity.TurretBase;

import java.util.List;
import java.util.Random;

public class RocketProjectile extends TurretProjectile {
    public int arrowShake;
    public float accuracy;
    private Entity target;
    private float speed = 0.06F;

    public RocketProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }

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
        if (ticksExisted >= 100) {
            this.setDead();
        }
        if (!worldObj.isRemote) {
            if (ConfigHandler.canRocketsHome && target != null) {
                double d0 = target.posX - this.posX;
                double d1 = target.posY + (double) target.getEyeHeight() - 1.1F - this.posY;
                double d2 = target.posZ - this.posZ;

                this.setThrowableHeading(d0, d1, d2, speed, 0.0F);
            } else if (ConfigHandler.canRocketsHome && target == null) {
                this.setDead();
            }
        }

        for (int i = 0; i <= 20; i++) {
            Random random = new Random();
            worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX + (random.nextGaussian() / 10), posY + (random.nextGaussian() / 10),
                    posZ + (random.nextGaussian() / 10), (0), (0), (0));
        }
    }

    @Override
    protected void onImpact(RayTraceResult movingobjectposition) {

        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState hitBlock = worldObj.getBlockState(movingobjectposition.getBlockPos());

            if( hitBlock.getBlock() instanceof BlockAbstractTurretHead)
            {
                return;
            }

            if (hitBlock != null && !hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (!worldObj.isRemote) {

            worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
            AxisAlignedBB axis = new AxisAlignedBB(this.posX - 5, this.posY - 5, this.posZ - 5,
                    this.posX + 5, this.posY + 5, this.posZ + 5);
            List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

            for (Entity mob : targets) {

                int damage = ConfigHandler.getRocketTurretSettings().getDamage();

                if (isAmped) {
                    if (mob instanceof EntityLivingBase) {
                        EntityLivingBase elb = (EntityLivingBase) mob;
                        damage += ((int) elb.getHealth() * (0.25 * amp_level));
                    }
                }

                if (mob instanceof EntityPlayer) {
                    if (canDamagePlayer((EntityPlayer) mob)) {
                        mob.attackEntityFrom(new NormalDamageSource("rocket"), damage);
                        mob.hurtResistantTime = 0;
                    }
                }

                if (ConfigHandler.isCanRocketsHurtEnderDragon() && mob instanceof EntityDragon) {
                    ((EntityDragon) mob).setHealth(((EntityDragon) mob).getHealth() - damage);
                    mob.hurtResistantTime = 0;
                } else {
                    mob.attackEntityFrom(new NormalDamageSource("rocket"), damage);
                    mob.hurtResistantTime = 0;
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

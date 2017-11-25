package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.OMTConfigHandler;
import omtteam.openmodularturrets.tileentity.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;

import static omtteam.omlib.util.compat.WorldTools.spawnEntity;

public class DisposableTurretProjectile extends TurretProjectile {
    private EntityItem itemBound;
    private boolean spawned = false;

    @SuppressWarnings("unused")
    public DisposableTurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.03F;
    }

    public DisposableTurretProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, ammo, turretBase);
        this.gravity = 0.03F;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!spawned && !this.getEntityWorld().isRemote) {
            itemBound = new EntityItem(this.getEntityWorld(), posX, posY - 0.2F, posZ, ammo);
            itemBound.motionX = this.motionX;
            itemBound.motionY = this.motionY + this.gravity;
            itemBound.motionZ = this.motionZ;
            itemBound.setPickupDelay(10000);
            itemBound.lifespan = 6000;
            spawnEntity(this.getEntityWorld(), itemBound);
            spawned = true;
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

        if (itemBound != null) {
            itemBound.setDead();
        }
        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile)) {

            int damage = OMTConfigHandler.getDisposableTurretSettings().getDamage();

            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getDamageAmpBonus() * amp_level));
                }
            }

            if (entity instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) entity)) {
                    entity.attackEntityFrom(new NormalDamageSource("disposable", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
                    entity.hurtResistantTime = 0;
                } else {
                    return;
                }
            } else if (canDamageEntity(entity)) {
                setTagsForTurretHit(entity);
                entity.attackEntityFrom(new NormalDamageSource("disposable", fakeDrops, turretBase, (WorldServer) this.getEntityWorld()), damage);
                entity.hurtResistantTime = 0;
            } else {
                return;
            }

            if (itemBound != null) {
                itemBound.setDead();
            }
            this.setDead();
        }
    }

    public EntityItem getItemBound() {
        return itemBound;
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
        return OMTConfigHandler.getDisposableTurretSettings().getDamageAmp();
    }
}
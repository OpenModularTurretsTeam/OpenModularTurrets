package omtteam.openmodularturrets.entity.projectiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.handler.ConfigHandler;
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
    public void onEntityUpdate() {
        if (!spawned && !this.getEntityWorld().isRemote && ticksExisted >= 2) {
            itemBound = new EntityItem(this.getEntityWorld(), posX, posY, posZ, ammo);
            itemBound.motionX = this.motionX;
            itemBound.motionY = this.motionY + 0.1F;
            itemBound.motionZ = this.motionZ;
            itemBound.setPickupDelay(100);
            spawnEntity(this.getEntityWorld(), itemBound);
            spawned = true;
        }

        if (ticksExisted > 100) {
            this.setDead();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void onImpact(RayTraceResult movingobjectposition) {

        if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK) {
            IBlockState hitBlock = getEntityWorld().getBlockState(movingobjectposition.getBlockPos());

            if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
                return;
            }

            if (!hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !getEntityWorld().isRemote) {
            if (movingobjectposition.typeOfHit.equals(RayTraceResult.Type.MISS)) {
                if (getEntityWorld().isAirBlock(movingobjectposition.getBlockPos())) {
                    return;
                }
            }

            int damage = ConfigHandler.getDisposableTurretSettings().getDamage();

            if (isAmped) {
                if (movingobjectposition.entityHit instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) movingobjectposition.entityHit;
                    damage += ((int) elb.getHealth() * (0.05F * amp_level));
                }
            }

            if (movingobjectposition.entityHit instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) movingobjectposition.entityHit)) {
                    movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("disposable"), damage);
                    movingobjectposition.entityHit.hurtResistantTime = 0;
                }
            } else {
                movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("disposable"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;
            }
        }

        if (itemBound != null) {
            itemBound.setDead();
        }
        this.setDead();
    }

    public EntityItem getItemBound() {
        return itemBound;
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
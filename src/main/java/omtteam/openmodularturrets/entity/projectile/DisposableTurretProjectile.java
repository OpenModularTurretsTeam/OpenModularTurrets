package omtteam.openmodularturrets.entity.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.block.turret.BlockTurret;
import omtteam.openmodularturrets.config.DisposableAmmo;
import omtteam.openmodularturrets.config.OMTConfig;
import omtteam.openmodularturrets.tileentity.base.TurretBase;

import javax.annotation.ParametersAreNonnullByDefault;

public class DisposableTurretProjectile extends TurretProjectile {
    private EntityItem itemBound;
    private boolean spawned = false;

    @SuppressWarnings("unused")
    public DisposableTurretProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.03F;
    }

    public DisposableTurretProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
        super(par1World, shownItem(ammo), turretBase);
        this.gravity = 0.03F;
    }

    /**
     * The item the projectile carries, one of the drawn ammo (1.20.1+); without drawn ammo (creative upgrade, ammo
     * disabled in the config) a cobblestone, so the projectile is not invisible and deals cobblestone's bonus.
     */
    private static ItemStack shownItem(ItemStack ammo) {
        if (ammo.isEmpty()) return new ItemStack(Blocks.COBBLESTONE);
        ItemStack item = ammo.copy();
        item.setCount(1);
        return item;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!spawned && !this.getEntityWorld().isRemote) {
            itemBound = new EntityItem(this.getEntityWorld(), posX, posY - 0.2F, posZ, ammo);
            itemBound.motionX = this.motionX;
            itemBound.motionY = this.motionY + this.gravity;
            itemBound.motionZ = this.motionZ;
            itemBound.setPickupDelay(100000);
            itemBound.lifespan = 40;
            world.spawnEntity(itemBound);
            spawned = true;
        }
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

        if (itemBound != null) {
            itemBound.setDead();
        }
        this.setDead();
    }

    @Override
    public void onHitEntity(Entity entity) {
        if (entity != null && !getEntityWorld().isRemote && !(entity instanceof TurretProjectile) && !this.isDead) {
            int damage = OMTConfig.TURRETS.disposable_turret.baseDamage;
            if (ammo != null) {
                damage += DisposableAmmo.getDamage(ammo);
            }
            if (isAmped) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase elb = (EntityLivingBase) entity;
                    damage += ((int) elb.getHealth() * (getDamageAmpBonus() * amp_level));
                }
            }

            if (canDamageEntity(entity)) {
                if (!(entity instanceof EntityPlayer)) setTagsForTurretHit(entity);
                entity.attackEntityFrom(new NormalDamageSource("disposable", fakeDrops, turretBase, (WorldServer) this.getEntityWorld(), true, entity), damage);
                if (damage > 0) notifyHit(entity);
                entity.hurtResistantTime = -1;
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
        return OMTConfig.TURRETS.disposable_turret.damageAmp;
    }
}
package openmodularturrets.entity.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

public class BulletProjectile extends TurretProjectile {

    public BulletProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
        this.gravity = 0.00F;
    }

    public BulletProjectile(World par1World, ItemStack ammo, TurretBase turretBase) {
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
    protected void onImpact(MovingObjectPosition movingobjectposition) {

        if (this.ticksExisted <= 1) {
            return;
        }
        if (movingobjectposition.typeOfHit == movingobjectposition.typeOfHit.BLOCK) {
            Block hitBlock = worldObj.getBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                               movingobjectposition.blockZ);
            if (hitBlock != null && !hitBlock.getMaterial().isSolid()) {
                // Go through non solid block
                return;
            }
        }

        if (movingobjectposition.entityHit != null && !worldObj.isRemote) {
            if (movingobjectposition.typeOfHit.equals(0)) {
                if (worldObj.isAirBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                        movingobjectposition.blockZ)) {
                    return;
                }
            }

            int damage = ConfigHandler.getMachineGunTurretSettings().getDamage();

            if (isAmped) {
                damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
            }

            if (movingobjectposition.entityHit instanceof EntityPlayer) {
                if (canDamagePlayer((EntityPlayer) movingobjectposition.entityHit)) {
                    movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                    movingobjectposition.entityHit.hurtResistantTime = 0;
                }
            } else {
                movingobjectposition.entityHit.attackEntityFrom(new NormalDamageSource("bullet"), damage);
                movingobjectposition.entityHit.hurtResistantTime = 0;
            }
        }

        if (movingobjectposition.entityHit == null && !worldObj.isRemote) {
            worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:bulletHit", 1.0F, 1.0F);
        }
        this.setDead();
    }

    @Override
    protected void updateFallState(double par1, boolean par3) {
        this.posY = posY + 12F;
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
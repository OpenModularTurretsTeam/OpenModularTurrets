package openmodularturrets.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.List;

public class GrenadeProjectile extends TurretProjectile {
    private boolean isAmped;

    public GrenadeProjectile(World par1World) {
        super(par1World);
        this.gravity = 0.00F;
    }

    public GrenadeProjectile(World world, ItemStack ammo, TurretBase turretBase) {
        super(world, ammo, turretBase);
        this.gravity = 0.03F;
    }

    @Override
    public void onEntityUpdate() {
        if (ticksExisted >= 50) {
            if (!worldObj.isRemote) {
                worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
                AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.posX - 3, this.posY - 3, this.posZ - 3,
                                                                  this.posX + 3, this.posY + 3, this.posZ + 3);
                List<Entity> targets = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axis);

                for (Entity mob : targets) {

                    int damage = ConfigHandler.getGrenadeTurretSettings().getDamage();

                    if (isAmped) {
                        if (mob instanceof EntityLivingBase) {
                            EntityLivingBase elb = (EntityLivingBase) mob;
                            damage += ((int) elb.getHealth() * (0.25 * amp_level));
                        }
                    }

                    if (mob instanceof EntityPlayer) {
                        if (canDamagePlayer((EntityPlayer) mob)) {
                            mob.attackEntityFrom(new NormalDamageSource("grenade"), damage * 0.9F);
                            mob.attackEntityFrom(new ArmorBypassDamageSource("grenade"), damage * 0.1F);
                            mob.hurtResistantTime = 0;
                        }
                    } else {
                        mob.attackEntityFrom(new NormalDamageSource("grenade"), damage * 0.9F);
                        mob.attackEntityFrom(new ArmorBypassDamageSource("grenade"), damage * 0.1F);
                        mob.hurtResistantTime = 0;
                    }

                }
            }
            this.setDead();
        }

        for (int i = 0; i <= 20; i++) {
            worldObj.spawnParticle("reddust", posX, posY, posZ, 1.0D, 1.0D, 1.0D);
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {
        if (this.ticksExisted >= 2) {
            this.motionX = 0.0F;
            this.motionY = 0.0F;
            this.motionZ = 0.0F;
        }
    }

    @Override
    protected float getGravityVelocity() {
        return this.gravity;
    }
}
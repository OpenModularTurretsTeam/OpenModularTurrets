package openmodularturrets.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;
import openmodularturrets.tileentity.turretbase.TurretBase;

import java.util.List;
import java.util.Random;

public class RocketProjectile extends TurretProjectile {

    private int ticksAlive = 0;
    private Entity target;
    public float speed = 0.03F;
    public int upwardsFirst = 15;
    public float yaw;
    public int arrowShake;
    public float accuracy;

    public RocketProjectile(World p_i1776_1_) {
        super(p_i1776_1_);
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

        if (target != null) {
            double d0 = target.posX - this.posX;
            double d1 = target.posY + (double) target.getEyeHeight() - 1.1F - this.posY;
            double d2 = target.posZ - this.posZ;

            this.setThrowableHeading(d0, d1, d2, speed, 0.0F);
            speed = speed + 0.3F;

            double dX = (target.posX) - (this.posX);
            double dZ = (target.posZ) - (this.posZ);
            yaw = ((float) (Math.atan2(dZ, dX))) - 1.570796F;
        }

        for (int i = 0; i <= 25; i++) {
            Random random = new Random();
            worldObj.spawnParticle("smoke", posX + (random.nextGaussian() / 10), posY + (random.nextGaussian() / 10),
                                   posZ + (random.nextGaussian() / 10), (0), (0), (0));
        }
    }

    @Override
    protected void onImpact(MovingObjectPosition movingobjectposition) {

        if (this.ticksExisted <= 5) {
            return;
        }

        if (movingobjectposition.typeOfHit.equals(0)) {
            if (worldObj.isAirBlock(movingobjectposition.blockX, movingobjectposition.blockY,
                                    movingobjectposition.blockZ)) {
                return;
            }
        }

        if (!worldObj.isRemote) {

            worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
            AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(this.posX - 5, this.posY - 5, this.posZ - 5,
                                                              this.posX + 5, this.posY + 5, this.posZ + 5);
            List<Entity> targets = worldObj.getEntitiesWithinAABB(Entity.class, axis);

            for (Entity mob : targets) {
                int damage = ConfigHandler.getRocketTurretSettings().getDamage();

                if (isAmped) {
                    damage += ConfigHandler.getDamageAmpDmgBonus() * amp_level;
                }

                if (mob instanceof EntityPlayer) {
                    if (canDamagePlayer((EntityPlayer) mob)) {
                        mob.attackEntityFrom(new NormalDamageSource("rocket"), damage);
                        mob.hurtResistantTime = 0;
                    }
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
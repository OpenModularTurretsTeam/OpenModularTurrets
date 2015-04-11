package openmodularturrets.entity.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import openmodularturrets.entity.projectiles.damagesources.ArmorBypassDamageSource;
import openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import openmodularturrets.handler.ConfigHandler;

import java.util.List;

public class GrenadeProjectile extends TurretProjectile {
    public boolean isAmped;

    public GrenadeProjectile(World world) {
        super(world);
        this.gravity = 0.03F;
    }

    public GrenadeProjectile(World world, ItemStack ammo) {
        super(world, ammo);
        this.gravity = 0.03F;
    }

    @Override
    public void onEntityUpdate() {
        if (ticksExisted >= 50) {

            if (!worldObj.isRemote) {

                worldObj.createExplosion(null, posX, posY, posZ, 0.1F, true);
                AxisAlignedBB axis = AxisAlignedBB.getBoundingBox(
                        this.posX - 3, this.posY - 3, this.posZ - 3,
                        this.posX + 3, this.posY + 3, this.posZ + 3);
                List<Entity> targets = worldObj.getEntitiesWithinAABB(
                        Entity.class, axis);

                for (Entity mob : targets) {
                    int damage = ConfigHandler.getGrenadeTurretSettings()
                            .getDamage();

                    if (isAmped) {
                        damage += ConfigHandler.getDamageAmpDmgBonus()
                                * amp_level;
                    }

                    mob.attackEntityFrom(new NormalDamageSource("grenade"), damage * 0.9F);
                    mob.attackEntityFrom(new ArmorBypassDamageSource("grenade"), damage * 0.1F);
                    mob.hurtResistantTime = 0;
                }
            }
            this.setDead();
        }

        for (int i = 0; i <= 20; i++) {
            worldObj.spawnParticle("reddust", posX, posY, posZ, 1.0D, 1.0D,
                    1.0D);
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
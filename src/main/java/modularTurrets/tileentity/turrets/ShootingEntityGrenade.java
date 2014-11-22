package modularTurrets.tileentity.turrets;

import modularTurrets.projectiles.GrenadeProjectile;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ShootingEntityGrenade extends EntityLiving implements IRangedAttackMob {
    public ItemStack stack;

    public ShootingEntityGrenade(World par1World, ItemStack stack) {
	super(par1World);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f, float accuracy, TurretBase base) {
        if (stack != null) {
            GrenadeProjectile entityCrap = new GrenadeProjectile(worldObj, posX, posY + 1.5F, posZ);

            if (TurretHeadUtils.hasDamageAmpAddon(base)) {
                worldObj.playSoundEffect(posX, posY, posZ, "modularturrets:amped", 1.0F, 1.0F);
                entityCrap.isAmped = true;
            }

            double d0 = entitylivingbase.posX - this.posX;
            double d1 = entitylivingbase.posY - this.posY;
            double d2 = entitylivingbase.posZ - this.posZ;
            float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * (0.2F * (getDistanceToEntity(entitylivingbase) * 0.04F));
            entityCrap.setThrowableHeading(d0, d1 + (double) f1, d2, 1.5F, accuracy);

            worldObj.playSoundEffect(posX, posY, posZ, "modularturrets:grenade", 1.0F, 1.0F);

            if (!worldObj.isRemote) {
                this.worldObj.spawnEntityInWorld(entityCrap);
            }

            for (int i = 0; i <= 20; i++) {
                worldObj.spawnParticle("reddust", posX, posY + 1.8F, posZ, 1.0D, 1.0D, 1.0D);
            }

            stack = null;
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {

    }
}

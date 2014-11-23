package modularTurrets.tileEntities.turrets;

import modularTurrets.projectiles.RocketProjectile;
import modularTurrets.tileEntities.turretBase.TurretBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ShootingEntityRocket extends EntityLiving implements
	IRangedAttackMob {

    ItemStack stack;

    public ShootingEntityRocket(World par1World, ItemStack stack) {
	super(par1World);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase,
	    float f, float accuraccy, TurretBase base) {

	if (stack != null) {
	    RocketProjectile entityCrap = new RocketProjectile(worldObj, posX,
		    posY + 1.5F, posZ, entitylivingbase);
	    if (TurretHeadUtils.hasDamageAmpAddon(base)) {
		worldObj.playSoundEffect(posX, posY, posZ,
			"modularturrets:amped", 1.0F, 1.0F);
		entityCrap.isAmped = true;
	    }
	    double d0 = entitylivingbase.posX - this.posX;
	    double d1 = entitylivingbase.posY
		    + (double) entitylivingbase.getEyeHeight()
		    - 2.500000023841858D - this.posY;
	    double d2 = entitylivingbase.posZ - this.posZ;
	    float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
	    entityCrap.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F,
		    accuraccy);
	    entityCrap.accuraccy = accuraccy;
	    worldObj.playSoundEffect(posX, posY, posZ, "modularturrets:rocket",
		    2.0F, 1.0F);
	    this.worldObj.spawnEntityInWorld(entityCrap);
	}
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase,
	    float f) {
    }
}

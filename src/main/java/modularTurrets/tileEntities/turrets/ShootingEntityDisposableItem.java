package modularTurrets.tileEntities.turrets;

import modularTurrets.misc.Constants;
import modularTurrets.projectiles.DisposableTurretProjectile;
import modularTurrets.tileEntities.turretBase.TurretBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ShootingEntityDisposableItem extends EntityLiving implements
	IRangedAttackMob {

    ItemStack stack;

    public ShootingEntityDisposableItem(World par1World, ItemStack stack) {
	super(par1World);
	this.stack = stack;
    }

    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase,
	    float f, float accuraccy, TurretBase base) {
	if (stack != null) {
	    DisposableTurretProjectile entityCrap = new DisposableTurretProjectile(
		    worldObj, posX, posY + 1.5F, posZ, stack);
	    if (TurretHeadUtils.hasDamageAmpAddon(base)) {
		worldObj.playSoundEffect(posX, posY, posZ,
			"modularturrets:amped", 1.0F, 1.0F);
		entityCrap.isAmped = true;
	    }
	    double d0 = entitylivingbase.posX - this.posX;
	    double d1 = entitylivingbase.posY
		    + (double) entitylivingbase.getEyeHeight() - 2.5F
		    - this.posY;
	    double d2 = entitylivingbase.posZ - this.posZ;
	    float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2)
		    * (0.2F * (getDistanceToEntity(entitylivingbase) * 0.04F));
	    entityCrap.setThrowableHeading(d0, d1 + (double) f1, d2, 1.6F,
		    accuraccy);
	    worldObj.playSoundEffect(posX, posY, posZ,
		    "modularturrets:disposable", 1.0F, 1.0F);

	    if (!worldObj.isRemote) {
		worldObj.spawnEntityInWorld(entityCrap);
	    }
	    stack = null;
	}
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase,
	    float f) {
    }
}

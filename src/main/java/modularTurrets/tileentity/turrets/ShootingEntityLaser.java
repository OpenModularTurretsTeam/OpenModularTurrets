package modularTurrets.tileentity.turrets;

import modularTurrets.projectiles.LaserProjectile;
import modularTurrets.tileentity.turretBase.TurretBase;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ShootingEntityLaser extends EntityLiving implements IRangedAttackMob {
    public ShootingEntityLaser(World par1World) {
	super(par1World);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f, float accuraccy, TurretBase base) {

        LaserProjectile entityCrap = new LaserProjectile(worldObj, posX, posY + 1.8F, posZ, entitylivingbase);

        if (TurretHeadUtils.hasDamageAmpAddon(base)) {
            worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:amped", 1.0F, 1.0F);
            entityCrap.isAmped = true;
        }

        double d0 = entitylivingbase.posX - this.posX;
        double d1 = entitylivingbase.posY - this.posY;
        double d2 = entitylivingbase.posZ - this.posZ;
        float f1 = MathHelper.sqrt_double(d0 * d0 + d2 * d2) * 0.2F;
        entityCrap.setThrowableHeading(d0, d1, d2, 5.0F, accuraccy);
        worldObj.playSoundEffect(posX, posY, posZ, "openmodularturrets:laser", 0.5F, 1.0F);
        this.worldObj.spawnEntityInWorld(entityCrap);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase entitylivingbase, float f) {

    }
}

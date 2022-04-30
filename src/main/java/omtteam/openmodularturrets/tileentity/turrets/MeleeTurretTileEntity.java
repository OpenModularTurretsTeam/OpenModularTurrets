package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import omtteam.openmodularturrets.entity.projectiles.damagesources.NormalDamageSource;
import omtteam.openmodularturrets.init.ModSounds;
import omtteam.openmodularturrets.turret.TurretHeadUtil;

import javax.annotation.Nonnull;
import java.util.List;

import static omtteam.openmodularturrets.util.OMTUtil.canDamageEntity;

public class MeleeTurretTileEntity extends TurretHead {
    public MeleeTurretTileEntity() {
        super(1);
    }

    @Override
    public void update() {
        super.update();
        if (this.updateChecks()) {
            float damage = this.getTurretType().getSettings().baseDamage;
            int fakeDrops = TurretHeadUtil.getFakeDropsLevel(base);
            List<EntityLivingBase> entityList = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.getPos()).expand(3D, 1D, 3D));
            for (EntityLivingBase entity : entityList) {
                if (canDamageEntity(entity, this.getBase())) {
                    entity.attackEntityFrom(new NormalDamageSource("melee", fakeDrops, this.getBase(), (WorldServer) this.getWorld(), false), damage);
                }
            }
            this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);
        }
    }

    @Override
    public boolean requiresAmmo() {
        return false;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return false;
    }

    @Override
    public ItemStack getAmmo() {
        return null;
    }

    @Override
    public Integer[] getDefaultPriorities() {
        return new Integer[]{1, 1, 0, 10};
    }

    @Override
    @Nonnull
    protected SoundEvent getLaunchSoundEffect() {
        return ModSounds.relativisticLaunchSound;
    } // TODO: Soundeffects
}

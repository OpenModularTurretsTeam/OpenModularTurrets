package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class RelativisticTurretTileEntity extends TurretHead {
    public RelativisticTurretTileEntity() {
        super(3);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void update() {
        super.update();
        if (this.updateChecks()) {
            target.addPotionEffect(new PotionEffect(Potion.getPotionById(2), 200, 3, false, true));
            target.addPotionEffect(new PotionEffect(Potion.getPotionById(18), 200, 3, false, true));
            this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);
            target = null;
        }
    }

    @Override
    public boolean isEntityValidTarget(EntityLivingBase entity) {
        return !entity.isPotionActive(Potion.getPotionById(2));
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
    }
}

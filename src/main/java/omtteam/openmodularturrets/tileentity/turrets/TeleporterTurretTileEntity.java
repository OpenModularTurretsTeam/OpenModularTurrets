package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import omtteam.openmodularturrets.blocks.turretheads.BlockTeleporterTurret;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class TeleporterTurretTileEntity extends TurretHead {
    public TeleporterTurretTileEntity() {
        super(4);
    }

    @Override
    public void update() {
        super.update();
        if (this.updateChecks()) {
            Vec3d basePositionToSet = new Vec3d(this.getPos().getX() + 0.5F, this.getPos().getY() + 1.0F, this.getPos().getZ() + 0.5F);
            target.setPositionAndUpdate(basePositionToSet.x, basePositionToSet.y, basePositionToSet.z);
            this.getWorld().playSound(null, this.getPos(), this.getLaunchSoundEffect(), SoundCategory.BLOCKS, 0.6F, 1.0F);
            ((BlockTeleporterTurret) this.getWorld().getBlockState(this.pos).getBlock()).shouldAnimate = true;
            target = null;
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
        return new Integer[]{1, -10, 0, 10};
    }

    @Override
    @Nonnull
    protected SoundEvent getLaunchSoundEffect() {
        return ModSounds.teleportLaunchSound;
    }
}

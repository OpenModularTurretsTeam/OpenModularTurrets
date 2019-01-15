package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.omlib.api.render.ColorOM;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.render.MessageRenderRay;
import omtteam.omlib.util.EntityUtil;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModSounds;

public class LaserTurretTileEntity extends RayTracingTurret {
    private ColorOM color = new ColorOM(1F, 0.1F, 0, 0.38F);

    public LaserTurretTileEntity() {
        super(5);
    }

    @Override
    public int getTurretRange() {
        return OMTConfig.TURRETS.laser_turret.getBaseRange();
    }

    @Override
    public int getTurretPowerUsage() {
        return OMTConfig.TURRETS.laser_turret.getPowerUsage();
    }

    @Override
    public int getTurretFireRate() {
        return OMTConfig.TURRETS.laser_turret.getBaseFireRate();
    }

    @Override
    public double getTurretAccuracy() {
        return OMTConfig.TURRETS.laser_turret.getBaseAccuracyDeviation();
    }

    @Override
    public double getTurretDamageAmpBonus() {
        return OMTConfig.TURRETS.laser_turret.getDamageAmp();
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
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.laserLaunchSound;
    }

    @Override
    protected void doTargetedShot(Entity target, ItemStack ammo) {
        shootRay(target.posX, target.posY + target.getEyeHeight(), target.posZ, getTurretAccuracy());
    }

    @Override
    protected void renderRay(Vec3d start, Vec3d end) {
        OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                new MessageRenderRay(start, end, color, 5, true),
                new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                start.x, start.y, start.z, 120));
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModSounds.laserHitSound;
    }

    @Override
    protected float getDamageModifier(Entity entity) {
        return (30 - EntityUtil.getEntityArmor(entity)) / 20F + 0.1F; //0.6x to 1.6x damage multiplicator
    }

    @Override
    protected float getNormalDamageFactor() {
        return 1F;
    }

    @Override
    protected float getBypassDamageFactor() {
        return 0;
    }

    @Override
    protected void applyHitEffects(Entity entity) {

    }

    @Override
    protected void applyLaunchEffects() {

    }

    @Override
    protected void handleBlockHit(IBlockState hitBlock, BlockPos pos) {

    }
}

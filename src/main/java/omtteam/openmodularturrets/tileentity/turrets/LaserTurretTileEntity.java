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
import omtteam.openmodularturrets.compatibility.ModCompatibility;
import omtteam.openmodularturrets.init.ModSounds;
import valkyrienwarfare.api.IPhysicsEntity;
import valkyrienwarfare.api.IPhysicsEntityManager;
import valkyrienwarfare.api.TransformType;

public class LaserTurretTileEntity extends RayTracingTurret {
    private ColorOM color = new ColorOM(1F, 0.1F, 0, 0.38F);

    public LaserTurretTileEntity() {
        super(5);
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
        Vec3d targetPos = target.getPositionVector();
        double d0 = targetPos.x;
        double d1 = targetPos.y + target.getEyeHeight();
        double d2 = targetPos.z;
        if (ModCompatibility.ValkyrienWarfareLoaded) {

            IPhysicsEntity physicsEntity = IPhysicsEntityManager.INSTANCE.getPhysicsEntityFromShipSpace(getWorld(),
                    getPos());
            if (physicsEntity != null) {
                Vec3d targetPosInShip = physicsEntity.transformVector(targetPos, TransformType.GLOBAL_TO_SUBSPACE);
                d0 = targetPosInShip.x;
                d1 = targetPosInShip.y + target.getEyeHeight();
                d2 = targetPosInShip.z;
            }
        }
        shootRay(d0, d1, d2, getBaseTurretAccuracy());
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

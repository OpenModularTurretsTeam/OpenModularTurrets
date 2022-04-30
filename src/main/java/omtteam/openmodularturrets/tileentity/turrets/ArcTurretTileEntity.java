package omtteam.openmodularturrets.tileentity.turrets;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import omtteam.omlib.network.OMLibNetworkingHandler;
import omtteam.omlib.network.messages.render.MessageRenderLightning;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static omtteam.openmodularturrets.util.OMTUtil.canDamageEntity;

public class ArcTurretTileEntity extends RayTracingTurret {
    private final List<Integer> hitList = new ArrayList<>();
    private int hop = 0;

    public ArcTurretTileEntity() {
        super(4);
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
        return new Integer[]{5, 10, 2, -10, 10};
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.arcLaunchSound;
    }

    @Override
    protected void renderRay(Vec3d start, Vec3d end) {
        OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                new MessageRenderLightning(start, end, 3),
                new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                start.x, start.y, start.z, 120));
    }

    @Override
    protected Vec3d getRenderStartVector() {
        return new Vec3d(this.getPos().getX() + 0.5D,
                         this.getPos().getY() + 1D,
                         this.getPos().getZ() + 0.5D);
    }

    @Override
    protected SoundEvent getHitSound() {
        return null;
    }

    @Override
    protected float getDamageModifier(Entity entity) {
        return 1F / (3 - hop);
    }

    @Override
    protected float getNormalDamageFactor() {
        return 0.6F;
    }

    @Override
    protected float getBypassDamageFactor() {
        return 0.4F;
    }

    @Override
    protected void applyHitEffects(Entity entity) {
        if (this.hop > 0) {
            this.hop--;
            this.hitList.add(entity.getEntityId());
            int range = 3 * hop;
            AxisAlignedBB axis = new AxisAlignedBB(entity.posX - range, entity.posY - range, entity.posZ - range, entity.posX + range, entity.posY + range, entity.posZ + range);
            List<EntityLivingBase> entityList = this.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axis);
            for (EntityLivingBase entityLivingBase : entityList) {
                if (canDamageEntity(entityLivingBase, this.getBase()) && !hitList.contains(entityLivingBase.getEntityId())) {
                    this.applyHitEffects(entityLivingBase);
                    this.damageEntity(entityLivingBase);
                    this.renderRay(entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0), entityLivingBase.getPositionVector().addVector(0, entity.getEyeHeight(), 0));
                    break;
                }
            }
        }
    }

    @Override
    protected void applyLaunchEffects() {
        this.hop = 2;
        this.hitList.clear();
    }

    @Override
    protected void handleBlockHit(IBlockState hitBlock, BlockPos pos) {

    }
}

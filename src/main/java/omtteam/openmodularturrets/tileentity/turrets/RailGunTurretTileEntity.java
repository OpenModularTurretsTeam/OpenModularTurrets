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
import omtteam.openmodularturrets.blocks.turretheads.BlockAbstractTurretHead;
import omtteam.openmodularturrets.handler.config.OMTConfig;
import omtteam.openmodularturrets.init.ModItems;
import omtteam.openmodularturrets.init.ModSounds;

import javax.annotation.Nonnull;

public class RailGunTurretTileEntity extends RayTracingTurret {
    private final ColorOM color = new ColorOM(1F, 0.5F, 0, 0.2F);

    public RailGunTurretTileEntity() {
        super(5);
        ammo = new ItemStack(ModItems.ammoMetaItem, 1, 2);
    }

    @Override
    public boolean requiresAmmo() {
        return true;
    }

    @Override
    public boolean requiresSpecificAmmo() {
        return true;
    }

    @Override
    public Integer[] getDefaultPriorities() {
        return new Integer[]{10, 10, 20, 40, 10};
    }

    @Nonnull
    @Override
    public SoundEvent getLaunchSoundEffect() {
        return ModSounds.railgunLaunchSound;
    }

    @Override
    protected SoundEvent getHitSound() {
        return ModSounds.railGunHitSound;
    }

    @Override
    protected void renderRay(Vec3d start, Vec3d end) {
        OMLibNetworkingHandler.INSTANCE.sendToAllAround(
                new MessageRenderRay(start, end, color, 3, true),
                new NetworkRegistry.TargetPoint(this.getWorld().provider.getDimension(),
                                                start.x, start.y, start.z, 120));
    }

    @Override
    protected float getDamageModifier(Entity entity) {
        return (EntityUtil.getEntityArmor(entity)) / 20F + 0.6F;
    }

    @Override
    protected float getNormalDamageFactor() {
        return 0;
    }

    @Override
    protected float getBypassDamageFactor() {
        return 1;
    }

    @Override
    protected void applyHitEffects(Entity entity) {

    }

    @Override
    protected void applyLaunchEffects() {

    }

    @Override
    protected void handleBlockHit(IBlockState hitBlock, BlockPos pos) {
        if (hitBlock.getBlock() instanceof BlockAbstractTurretHead) {
            return;
        }

        if (!hitBlock.getMaterial().isSolid()) {
            return;
        } else if (OMTConfig.TURRETS.canRailgunDestroyBlocks) {
            if (hitBlock.getBlockHardness(this.world, pos) < 200) {
                this.getWorld().destroyBlock(pos, false);
            }
        }
    }
}

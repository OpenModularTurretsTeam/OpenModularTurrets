package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import omtteam.openmodularturrets.client.render.blockentity.LeverRenderer;

/**
 * The crank item shows the crank model; display transforms are in the item model JSON.
 */
public class LeverItemRenderer implements ItemStackRenderer {
    private LeverRenderer renderer;

    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (renderer == null) {
            renderer = new LeverRenderer(Minecraft.getInstance().getEntityModels().bakeLayer(LeverRenderer.LAYER));
        }
        renderer.render(Direction.SOUTH, 0F, poseStack, buffers, packedLight, packedOverlay);
    }
}

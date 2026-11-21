package omtteam.openmodularturrets.client.platform.neoforge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import omtteam.openmodularturrets.client.render.item.ItemStackRenderer;

/**
 * NeoForge view of an {@link ItemStackRenderer}: the {@link BlockEntityWithoutLevelRenderer} that NeoForge calls for
 * items whose model has {@code parent: builtin/entity}.
 */
public final class NeoForgeItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final ItemStackRenderer renderer;

    private NeoForgeItemRenderer(ItemStackRenderer renderer) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.renderer = renderer;
    }

    /** The client extensions of an item drawn by this renderer; the renderer is created on first use. */
    public static IClientItemExtensions extensions(ItemStackRenderer renderer) {
        return new IClientItemExtensions() {
            private NeoForgeItemRenderer itemRenderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (itemRenderer == null) {
                    itemRenderer = new NeoForgeItemRenderer(renderer);
                }
                return itemRenderer;
            }
        };
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                             int packedLight, int packedOverlay) {
        renderer.render(stack, context, poseStack, buffers, packedLight, packedOverlay);
    }
}

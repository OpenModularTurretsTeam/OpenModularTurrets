package omtteam.openmodularturrets.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Draws an item whose model has {@code parent: builtin/entity}. The loaders adapt it: Fabric's
 * {@code BuiltinItemRendererRegistry}, NeoForge's {@code IClientItemExtensions#getCustomRenderer}.
 */
@FunctionalInterface
public interface ItemStackRenderer {
    void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers,
                int packedLight, int packedOverlay);
}

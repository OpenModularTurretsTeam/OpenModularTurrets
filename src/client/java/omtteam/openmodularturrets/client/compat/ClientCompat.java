package omtteam.openmodularturrets.client.compat;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Small helpers around client-side vanilla APIs that change between Minecraft versions.
 */
public final class ClientCompat {
    private ClientCompat() {
    }

    @FunctionalInterface
    public interface TooltipAppender {
        void append(ItemStack stack, TooltipFlag flag, List<Component> lines);
    }

    /** Adds a position + colour vertex. */
    public static void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, int argb) {
        consumer.addVertex(pose, x, y, z).setColor(argb);
    }

    /**
     * Adds a full entity vertex (position, white colour, UV, no overlay, light, normal) for
     * {@code RenderTypes.entity*} buffers.
     */
    public static void entityVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float normalX, float normalY, float normalZ) {
        consumer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight).setNormal(pose, normalX, normalY, normalZ);
    }

    /** A pose stack starting at a copy of {@code pose}, for drawing model parts inside deferred custom geometry. */
    public static PoseStack poseStackAt(PoseStack.Pose pose) {
        PoseStack poseStack = new PoseStack();
        poseStack.last().set(pose);
        return poseStack;
    }

    /** Appends lines to item tooltips (called for every item). */
    public static void registerTooltip(TooltipAppender appender) {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> appender.append(stack, flag, lines));
    }
}

package omtteam.openmodularturrets.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.OverlayTexture;

import org.joml.Matrix4f;

/**
 * Hides client-side API differences between the supported Minecraft versions.
 */
public final class ClientCompat {
    private ClientCompat() {
    }

    /**
     * Draws the dimmed background of a container screen. Only needed on 1.20.1; on 1.21 {@code Screen.render}
     * already does it.
     */
    @SuppressWarnings("unused")
    public static void renderContainerBackground(Screen screen, GuiGraphics graphics, int mouseX, int mouseY,
                                                 float partialTick) {
        //? if <1.21 {
        /*screen.renderBackground(graphics);
        *///?}
    }

    /** Adds a position + colour vertex (the {@code VertexConsumer} API changed in 1.21). */
    public static void vertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int argb) {
        //? if >=1.21 {
        consumer.addVertex(pose, x, y, z).setColor(argb);
        //?} else {
        /*consumer.vertex(pose, x, y, z).color((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, (argb >>> 24) & 0xFF)
                .endVertex();
        *///?}
    }

    /**
     * Adds a full entity vertex (position, white colour, UV, no overlay, light, normal) for
     * {@code RenderType.entity*} buffers.
     */
    public static void entityVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z, float u, float v,
                                    int packedLight, float normalX, float normalY, float normalZ) {
        //? if >=1.21 {
        consumer.addVertex(pose, x, y, z).setColor(-1).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight).setNormal(pose, normalX, normalY, normalZ);
        //?} else {
        /*consumer.vertex(pose.pose(), x, y, z).color(255, 255, 255, 255).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight).normal(pose.normal(), normalX, normalY, normalZ).endVertex();
        *///?}
    }

}

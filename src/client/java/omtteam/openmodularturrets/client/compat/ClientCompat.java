package omtteam.openmodularturrets.client.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
//? if >=1.21.11 {
/*import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.shapes.Shapes;
*///?} else {
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
//?}

import org.joml.Matrix4f;

import java.util.List;

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

    /** Draws a part of a 256 × 256 GUI texture (1.21.11 also takes the render pipeline and the texture size). */
    public static void blit(GuiGraphics graphics, ResourceLocation texture, int x, int y, int u, int v, int width,
                            int height) {
        //? if >=1.21.11 {
        /*graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v, width, height, 256, 256);
        *///?} else {
        graphics.blit(texture, x, y, u, v, width, height);
        //?}
    }

    /** Shows a tooltip at the mouse; 1.21.11 draws it at the end of the frame. */
    public static void renderTooltip(GuiGraphics graphics, Font font, Component text, int mouseX, int mouseY) {
        //? if >=1.21.11 {
        /*graphics.setTooltipForNextFrame(font, text, mouseX, mouseY);
        *///?} else {
        graphics.renderTooltip(font, text, mouseX, mouseY);
        //?}
    }

    /** Shows a tooltip of wrapped lines at the mouse. */
    public static void renderTooltip(GuiGraphics graphics, Font font, List<FormattedCharSequence> lines, int mouseX,
                                     int mouseY) {
        //? if >=1.21.11 {
        /*graphics.setTooltipForNextFrame(font, lines, mouseX, mouseY);
        *///?} else {
        graphics.renderTooltip(font, lines, mouseX, mouseY);
        //?}
    }

    /** Shows a tooltip of several lines at a position. */
    public static void renderComponentTooltip(GuiGraphics graphics, Font font, List<Component> lines, int x, int y) {
        //? if >=1.21.11 {
        /*graphics.setComponentTooltipForNextFrame(font, lines, x, y);
        *///?} else {
        graphics.renderComponentTooltip(font, lines, x, y);
        //?}
    }

    /** The render type of lines in the world ({@code RenderTypes} on 1.21.11). */
    public static RenderType lines() {
        //? if >=1.21.11 {
        /*return RenderTypes.lines();
        *///?} else {
        return RenderType.lines();
        //?}
    }

    /** The render type of glowing beams (lightning). */
    public static RenderType lightning() {
        //? if >=1.21.11 {
        /*return RenderTypes.lightning();
        *///?} else {
        return RenderType.lightning();
        //?}
    }

    /** Draws the 12 edges of a box into a {@link #lines()} buffer; the colour components go from 0 to 1. */
    public static void renderLineBox(PoseStack poseStack, VertexConsumer lines, double minX, double minY, double minZ,
                                     double maxX, double maxY, double maxZ, float red, float green, float blue,
                                     float alpha) {
        //? if >=1.21.11 {
        /*// 1.21.11 lines carry their width; this is the one of the block outline
        ShapeRenderer.renderShape(poseStack, lines, Shapes.box(minX, minY, minZ, maxX, maxY, maxZ), 0, 0, 0,
                ARGB.colorFromFloat(alpha, red, green, blue), Minecraft.getInstance().getWindow().getAppropriateLineWidth());
        *///?} else {
        LevelRenderer.renderLineBox(poseStack, lines, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        //?}
    }

    /** Whether a shift key is held (1.21.11 asks the game instead of {@code Screen}). */
    public static boolean hasShiftDown() {
        //? if >=1.21.11 {
        /*return Minecraft.getInstance().hasShiftDown();
        *///?} else {
        return Screen.hasShiftDown();
        //?}
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

    /**
     * Turns the pose towards the camera and scales it for text, like a name tag: x right, y down. The camera rotation
     * changed in 1.20.5, so 1.20.1 mirrors x and 1.21 does not (a mirrored label faces away and is culled).
     */
    /** Partial tick of the frame being drawn (stands still while the game is paused). */
    public static float partialTick() {
        //? if >=1.21.11 {
        /*return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        *///?} else if >=1.21 {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        //?} else {
        /*return Minecraft.getInstance().getFrameTime();
        *///?}
    }

    public static void faceCamera(PoseStack poseStack, Camera camera, float scale) {
        poseStack.mulPose(camera.rotation());
        //? if >=1.21 {
        poseStack.scale(scale, -scale, scale);
        //?} else {
        /*poseStack.scale(-scale, -scale, scale);
        *///?}
    }
}

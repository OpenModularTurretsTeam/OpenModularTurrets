package omtteam.openmodularturrets.client.compat;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import org.joml.Matrix4f;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Hides client-side API differences between the supported Minecraft versions.
 */
public final class ClientCompat {
    private ClientCompat() {
    }

    @FunctionalInterface
    public interface TooltipAppender {
        void append(ItemStack stack, TooltipFlag flag, List<Component> lines);
    }

    /**
     * Draws the dimmed background of a container screen. Only needed on 1.20.1; on 1.21 {@code Screen.render}
     * already does it.
     */
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

    /**
     * Replaces baked models after they were baked. The modifier gets the model and the id of the top-level model
     * (block states and items, e.g. {@code mod:block#facing=north}; only namespace and path are meaningful).
     */
    public static void modifyModelsAfterBake(ModelLoadingPlugin.Context plugin,
                                             BiFunction<BakedModel, ResourceLocation, BakedModel> modifier) {
        //? if >=1.21 {
        plugin.modifyModelAfterBake().register((model, context) -> {
            var topLevel = context.topLevelId();
            return modifier.apply(model, topLevel != null ? topLevel.id() : context.resourceId());
        });
        //?} else {
        /*plugin.modifyModelAfterBake().register((model, context) -> modifier.apply(model, context.id()));
        *///?}
    }

    /** Appends lines to item tooltips (called for every item). */
    public static void registerTooltip(TooltipAppender appender) {
        //? if >=1.21 {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> appender.append(stack, flag, lines));
        //?} else {
        /*ItemTooltipCallback.EVENT.register((stack, flag, lines) -> appender.append(stack, flag, lines));
        *///?}
    }
}

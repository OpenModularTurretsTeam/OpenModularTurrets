package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.client.compat.ClientCompat;

/**
 * Arrow geometry (two crossed fins at the back and four crossed side quads), as vanilla's arrow renderer. The 1.12.2
 * version used it for the crossbow turret's arrows ({@code RenderTippedArrow}) and, with its own texture, for rockets
 * ({@code RocketRenderer}). The geometry is stretched to {@code halfLength}, so shorter projectiles such as the
 * crossbow bolt can use it as well.
 */
public class ArrowShapedRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation texture;
    /** As 1.12.2 {@code RocketRenderer}: skip the first frames so the projectile does not flash inside the turret. */
    private final int hiddenTicks;
    /** Half the length of the shaft in texture pixels (vanilla arrow: 8). */
    private final int halfLength;

    public ArrowShapedRenderer(EntityRendererProvider.Context context, ResourceLocation texture, int hiddenTicks) {
        this(context, texture, hiddenTicks, 8);
    }

    public ArrowShapedRenderer(EntityRendererProvider.Context context, ResourceLocation texture, int hiddenTicks,
                               int halfLength) {
        super(context);
        this.texture = texture;
        this.hiddenTicks = hiddenTicks;
        this.halfLength = halfLength;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        if (entity.tickCount < hiddenTicks) return;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-halfLength / 2.0F, 0.0F, 0.0F);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(texture));
        PoseStack.Pose pose = poseStack.last();
        int fin = 1 - halfLength; // the fins sit one pixel in front of the back end (vanilla: -7)
        float shaftU = halfLength * 2 / 32.0F; // vanilla: 0.5
        vertex(consumer, pose, fin, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        vertex(consumer, pose, fin, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        vertex(consumer, pose, fin, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        vertex(consumer, pose, fin, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
        vertex(consumer, pose, fin, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
        vertex(consumer, pose, fin, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        vertex(consumer, pose, fin, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        vertex(consumer, pose, fin, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);
        for (int i = 0; i < 4; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose = poseStack.last();
            vertex(consumer, pose, -halfLength, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            vertex(consumer, pose, halfLength, -2, 0, shaftU, 0.0F, 0, 1, 0, light);
            vertex(consumer, pose, halfLength, 2, 0, shaftU, 0.15625F, 0, 1, 0, light);
            vertex(consumer, pose, -halfLength, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
        }
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int x, int y, int z, float u, float v,
                               int normalX, int normalZ, int normalY, int light) {
        ClientCompat.entityVertex(consumer, pose, x, y, z, u, v, light, normalX, normalY, normalZ);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}

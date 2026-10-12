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
 * Arrow geometry (two crossed fins at the back and four crossed side quads), as vanilla's arrow renderer. oldmod used
 * it for the crossbow turret's arrows ({@code RenderTippedArrow}) and, with its own texture, for rockets
 * ({@code RocketRenderer}).
 */
public class ArrowShapedRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation texture;
    /** oldmod {@code RocketRenderer}: skip the first frames so the projectile does not flash inside the turret. */
    private final int hiddenTicks;

    public ArrowShapedRenderer(EntityRendererProvider.Context context, ResourceLocation texture, int hiddenTicks) {
        super(context);
        this.texture = texture;
        this.hiddenTicks = hiddenTicks;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        if (entity.tickCount < hiddenTicks) return;
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0F, 0.0F, 0.0F);
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(texture));
        PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        vertex(consumer, pose, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        vertex(consumer, pose, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        vertex(consumer, pose, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
        vertex(consumer, pose, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
        vertex(consumer, pose, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        vertex(consumer, pose, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        vertex(consumer, pose, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);
        for (int i = 0; i < 4; i++) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            pose = poseStack.last();
            vertex(consumer, pose, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            vertex(consumer, pose, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
            vertex(consumer, pose, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
            vertex(consumer, pose, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
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

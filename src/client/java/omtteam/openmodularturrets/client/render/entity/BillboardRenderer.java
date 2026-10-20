package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.client.compat.ClientCompat;

/**
 * A camera-facing sprite (1.12.2 used {@code RenderSnowball} with the "throwable bullet" item for bullets). Drawn from
 * both sides, so it is visible whichever way the camera orientation is defined.
 */
public class BillboardRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ResourceLocation texture;
    private final float size;

    public BillboardRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float size) {
        super(context);
        this.texture = texture;
        this.size = size;
    }

    @Override
    public void render(T entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light) {
        poseStack.pushPose();
        poseStack.scale(size, size, size);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutoutNoCull(texture));
        PoseStack.Pose pose = poseStack.last();
        ClientCompat.entityVertex(consumer, pose, -0.5F, -0.5F, 0, 0, 1, light, 0, 1, 0);
        ClientCompat.entityVertex(consumer, pose, 0.5F, -0.5F, 0, 1, 1, light, 0, 1, 0);
        ClientCompat.entityVertex(consumer, pose, 0.5F, 0.5F, 0, 1, 0, light, 0, 1, 0);
        ClientCompat.entityVertex(consumer, pose, -0.5F, 0.5F, 0, 0, 0, light, 0, 1, 0);
        poseStack.popPose();
        super.render(entity, yaw, partialTick, poseStack, buffers, light);
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public ResourceLocation getTextureLocation(T entity) {
        return texture;
    }
}

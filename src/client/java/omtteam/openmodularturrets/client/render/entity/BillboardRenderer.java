package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.client.compat.ClientCompat;

/**
 * A camera-facing sprite (oldmod used {@code RenderSnowball} with the "throwable bullet" item for bullets). Drawn
 * without culling, so it is visible whichever way the camera orientation is defined.
 */
public class BillboardRenderer<T extends Entity> extends EntityRenderer<T, EntityRenderState> {
    private final Identifier texture;
    private final float size;

    public BillboardRenderer(EntityRendererProvider.Context context, Identifier texture, float size) {
        super(context);
        this.texture = texture;
        this.size = size;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        int light = state.lightCoords;
        poseStack.pushPose();
        poseStack.scale(size, size, size);
        poseStack.rotate(camera.orientation);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutout(texture), (pose, consumer) -> {
            ClientCompat.entityVertex(consumer, pose, -0.5F, -0.5F, 0, 0, 1, light, 0, 1, 0);
            ClientCompat.entityVertex(consumer, pose, 0.5F, -0.5F, 0, 1, 1, light, 0, 1, 0);
            ClientCompat.entityVertex(consumer, pose, 0.5F, 0.5F, 0, 1, 0, light, 0, 1, 0);
            ClientCompat.entityVertex(consumer, pose, -0.5F, 0.5F, 0, 0, 0, light, 0, 1, 0);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }
}

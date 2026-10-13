package omtteam.openmodularturrets.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import omtteam.openmodularturrets.client.compat.ClientCompat;

/**
 * Arrow geometry (two crossed fins at the back and four crossed side quads), as vanilla's arrow renderer. oldmod used
 * it for the crossbow turret's arrows ({@code RenderTippedArrow}) and, with its own texture, for rockets
 * ({@code RocketRenderer}).
 */
public class ArrowShapedRenderer<T extends Entity> extends EntityRenderer<T, ArrowShapedRenderer.State> {
    private final Identifier texture;
    /** oldmod {@code RocketRenderer}: skip the first frames so the projectile does not flash inside the turret. */
    private final int hiddenTicks;

    public static class State extends EntityRenderState {
        float yRot;
        float xRot;
        boolean hidden;
    }

    public ArrowShapedRenderer(EntityRendererProvider.Context context, Identifier texture, int hiddenTicks) {
        super(context);
        this.texture = texture;
        this.hiddenTicks = hiddenTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(T entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        state.xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        state.hidden = entity.tickCount < hiddenTicks;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.hidden) {
            int light = state.lightCoords;
            poseStack.pushPose();
            poseStack.rotate(Axis.YP.rotationDegrees(state.yRot - 90.0F));
            poseStack.rotate(Axis.ZP.rotationDegrees(state.xRot));
            poseStack.rotate(Axis.XP.rotationDegrees(45.0F));
            poseStack.scale(0.05625F, 0.05625F, 0.05625F);
            poseStack.translate(-4.0F, 0.0F, 0.0F);
            collector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutCull(texture), (pose, consumer) -> {
                vertex(consumer, pose, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
                vertex(consumer, pose, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
                vertex(consumer, pose, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
                vertex(consumer, pose, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
                vertex(consumer, pose, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
                vertex(consumer, pose, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
                vertex(consumer, pose, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
                vertex(consumer, pose, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);
                PoseStack fins = ClientCompat.poseStackAt(pose);
                for (int i = 0; i < 4; i++) {
                    fins.rotate(Axis.XP.rotationDegrees(90.0F));
                    PoseStack.Pose finPose = fins.last();
                    vertex(consumer, finPose, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
                    vertex(consumer, finPose, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
                    vertex(consumer, finPose, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
                    vertex(consumer, finPose, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
                }
            });
            poseStack.popPose();
        }
        super.submit(state, poseStack, collector, camera);
    }

    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose, int x, int y, int z, float u, float v,
                               int normalX, int normalZ, int normalY, int light) {
        ClientCompat.entityVertex(consumer, pose, x, y, z, u, v, light, normalX, normalY, normalZ);
    }
}

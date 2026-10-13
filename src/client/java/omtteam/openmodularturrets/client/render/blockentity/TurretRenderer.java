package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.client.compat.ClientCompat;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

import java.util.Map;

/**
 * Turret renderer (oldmod {@code AbstractTurretRenderer}): orients the model on its mounting face with the frame
 * from {@link TurretMath}, then applies the interpolated yaw and pitch. Concealed turrets are not drawn.
 * Turrets without a ported model render nothing yet.
 */
public class TurretRenderer implements BlockEntityRenderer<TurretBlockEntity, TurretRenderer.State> {
    private final Map<TurretType, TurretModel> models;

    /** What the renderer needs from a turret, captured each frame. */
    public static class State extends BlockEntityRenderState {
        @Nullable
        TurretType type;
        boolean concealed;
        Face face = Face.UP;
        float yaw;
        float pitch;
    }

    public TurretRenderer(BlockEntityRendererProvider.Context context) {
        this.models = TurretModels.bake(context.entityModelSet());
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TurretBlockEntity turret, State state, float partialTick, Vec3 cameraPos,
                                   @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(turret, state, partialTick, cameraPos, breakProgress);
        state.type = turret.getTurretType();
        state.concealed = turret.getBlockState().getValue(TurretBlock.CONCEALED);
        state.face = turret.getFace();
        state.yaw = turret.getRenderYaw(partialTick);
        state.pitch = turret.getRenderPitch(partialTick);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.concealed || state.type == null) return;
        TurretModel model = models.get(state.type);
        if (model == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.rotate(rotationFor(state.face));
        submitModel(model, state.yaw, state.pitch, poseStack, collector, state.lightCoords);
        poseStack.popPose();
    }

    /**
     * Draws a model whose pose origin is the block centre in the turret's local frame. oldmod models are Y-down
     * with the pivot 16 pixels above the base plate: lift by one block and flip y and z.
     * <p>
     * The geometry is drawn later in the frame and the model parts are shared by all turrets of a type, so the aim
     * is applied right before drawing.
     */
    public static void submitModel(TurretModel model, float yaw, float pitch, PoseStack poseStack,
                                   SubmitNodeCollector collector, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutCull(model.getTexture()), (pose, buffer) -> {
            model.setAim(yaw, pitch);
            model.render(ClientCompat.poseStackAt(pose), buffer, packedLight, OverlayTexture.NO_OVERLAY);
        });
        poseStack.popPose();
    }

    /** Rotation from the local frame (x = right, y = up, z = forward) to world space. */
    public static Quaternionf rotationFor(Face face) {
        TurretMath.Frame frame = TurretMath.frame(face);
        Vector3 r = frame.right();
        Vector3 u = frame.up();
        Vector3 f = frame.forward();
        Matrix3f matrix = new Matrix3f(
                (float) r.x(), (float) r.y(), (float) r.z(),
                (float) u.x(), (float) u.y(), (float) u.z(),
                (float) f.x(), (float) f.y(), (float) f.z());
        return new Quaternionf().setFromNormalized(matrix);
    }

    /** Visible from further away than the 64 block default (turrets are often seen from range). */
    @Override
    public int getViewDistance() {
        return 96;
    }
}

package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
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
 * Turret renderer (1.12.2 {@code AbstractTurretRenderer}): orients the model on its mounting face with the frame
 * from {@link TurretMath}, then applies the interpolated yaw and pitch. Concealed turrets are not drawn.
 * <p>
 * The variant for 1.21.11: the turret's data is copied into a {@link State} when the frame is extracted, and drawn from
 * it in {@link #submit}.
 */
public class TurretRenderer implements BlockEntityRenderer<TurretBlockEntity, TurretRenderer.State> {
    /** What the renderer needs of a turret; {@code model} is {@code null} for a turret that is not drawn. */
    public static final class State extends BlockEntityRenderState {
        @Nullable
        TurretModel model;
        TurretModel.Pose pose = TurretModel.Pose.ITEM;
        Face face = Face.UP;
        @Nullable
        RenderType renderType;
    }

    private final Map<TurretType, TurretModel> models;

    public TurretRenderer(BlockEntityRendererProvider.Context context) {
        this.models = TurretModels.bake(context.entityModelSet());
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TurretBlockEntity turret, State state, float partialTick, Vec3 cameraPos,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        BlockEntityRenderer.super.extractRenderState(turret, state, partialTick, cameraPos, crumbling);
        state.model = null;
        if (turret.getBlockState().getValue(TurretBlock.CONCEALED)) return;
        TurretModel model = models.get(turret.getTurretType());
        if (model == null) return;
        state.model = model;
        state.pose = new TurretModel.Pose(turret.getRenderYaw(partialTick), turret.getRenderPitch(partialTick),
                turret.isActive(), turret.getRenderSpin(partialTick));
        state.face = turret.getFace();
        state.renderType = model.renderTypeFor(state.pose);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.model == null || state.renderType == null) return;
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(rotationFor(state.face));
        submitModel(state.model, state.pose, state.renderType, poseStack, collector, state.lightCoords,
                OverlayTexture.NO_OVERLAY, state.breakProgress);
        poseStack.popPose();
    }

    /**
     * Submits a model whose pose origin is the block centre in the turret's local frame. The 1.12.2 models are Y-down
     * with the pivot 16 pixels above the base plate: lift by one block and flip y and z.
     */
    public static void submitModel(TurretModel model, TurretModel.Pose pose, RenderType renderType, PoseStack poseStack,
                                   SubmitNodeCollector collector, int packedLight, int packedOverlay,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        collector.submitModel(model, pose, poseStack, renderType, packedLight, packedOverlay, 0, crumbling);
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

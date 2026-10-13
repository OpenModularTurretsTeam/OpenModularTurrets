package omtteam.openmodularturrets.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import omtteam.openmodularturrets.block.turret.TurretBlock;
import omtteam.openmodularturrets.blockentity.turret.TurretBlockEntity;
import omtteam.openmodularturrets.client.model.TurretModel;
import omtteam.openmodularturrets.client.model.TurretModels;
import omtteam.openmodularturrets.core.turret.TurretMath;
import omtteam.openmodularturrets.core.turret.TurretType;
import omtteam.openmodularturrets.core.world.Face;
import omtteam.openmodularturrets.core.world.Vector3;
import org.joml.Matrix3f;
import org.joml.Quaternionf;

import java.util.Map;

/**
 * Turret renderer (1.12.2 {@code AbstractTurretRenderer}): orients the model on its mounting face with the frame
 * from {@link TurretMath}, then applies the interpolated yaw and pitch. Concealed turrets are not drawn.
 * Turrets without a ported model render nothing yet.
 */
public class TurretRenderer implements BlockEntityRenderer<TurretBlockEntity> {
    private final Map<TurretType, TurretModel> models;

    public TurretRenderer(BlockEntityRendererProvider.Context context) {
        this.models = TurretModels.bake(context.getModelSet());
    }

    @Override
    public void render(TurretBlockEntity turret, float partialTick, PoseStack poseStack, MultiBufferSource buffers,
                       int packedLight, int packedOverlay) {
        if (turret.getBlockState().getValue(TurretBlock.CONCEALED)) return;
        TurretModel model = models.get(turret.getTurretType());
        if (model == null) return;
        model.setAim(turret.getRenderYaw(partialTick), turret.getRenderPitch(partialTick));
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(rotationFor(turret.getFace()));
        renderModel(model, poseStack, buffers, packedLight, packedOverlay);
        poseStack.popPose();
    }

    /**
     * Draws a model whose pose origin is the block centre in the turret's local frame. The 1.12.2 models are Y-down
     * with the pivot 16 pixels above the base plate: lift by one block and flip y and z.
     */
    public static void renderModel(TurretModel model, PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                                   int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.scale(1.0F, -1.0F, -1.0F);
        model.render(poseStack, buffers.getBuffer(RenderType.entityCutout(model.getTexture())), packedLight, packedOverlay);
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
